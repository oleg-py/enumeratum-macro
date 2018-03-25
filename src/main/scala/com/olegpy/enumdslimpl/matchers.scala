package com.olegpy.enumdslimpl

import scala.meta._


object matchers {
  object FallbackToEmptyCompanion {
    def unapply(a: Any): Option[(Defn, Defn.Object)] = a match {
      case Term.Block(Seq(cls @ TraitOrClass(_), companion: Defn.Object)) =>
        Some((cls, companion))

      case Term.Block(Seq(companion: Defn.Object, cls @ TraitOrClass(_))) =>
        Some((cls, companion))

      case cls @ TraitOrClass(toc) =>
        val name = Term.Name(toc.fold(_.name, _.name).value)
        Some((cls, q"""object $name"""))

      case _ => None
    }
  }

  object TraitOrClass {
    def unapply(defn: Defn): Option[Either[Defn.Class, Defn.Trait]] =
      defn match {
        case cls: Defn.Class => Some(Left(cls))
        case trt: Defn.Trait => Some(Right(trt))
        case _ => None
      }
  }
}
