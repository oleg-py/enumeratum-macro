package com.olegpy.enumdslimpl

import scala.meta._


object extractValues {
  private object ValueDef {
    private object ValueTerm {
      def unapply(arg: Term): Boolean = arg match {
        case Term.Name("Value") => true
        case _ => false
      }
    }

    private object MixTerm {
      def unapply(arg: Term): Boolean = arg match {
        case Term.ApplyType(Term.Select(ValueTerm(), Term.Name("mix")), _) => true
        case _ => false
      }
    }

    def unapply(arg: Term): Boolean = {
      arg match {
        case ValueTerm() | MixTerm() => true
        case Term.Apply(MixTerm() | ValueTerm(), _) => true
        case _ => false
      }
    }
  }

  def apply(toc: Either[Defn.Class, Defn.Trait]): (Either[Defn.Class, Defn.Trait], Seq[Stat]) = {
    val template = toc.fold(_.templ, _.templ)
    val (valDefns, others) = template.stats.getOrElse(Seq()).partition {
      case q"..$mods val ..$_ = ${ValueDef()}" =>
        if (mods.contains(mod"lazy")) {
          abort("Lazy val is meaningless for @enum values")
        }
        true
      case q"..$_ def $_ = ${ValueDef()}" =>
        abort("Value of @enum cannot be a def")
      case q"..$_ var ..$_ = ${ValueDef()}" =>
        abort("Value of @enum cannot be a var")
      case _ => false
    }

    val newTemplate = template.copy(stats = Some(others))
    val newToc = toc
      .left.map(_.copy(templ = newTemplate))
      .right.map(_.copy(templ = newTemplate))

    (newToc, valDefns)
  }
}
