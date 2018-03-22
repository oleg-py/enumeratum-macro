package com.olegpy.enumeratum

import scala.annotation.StaticAnnotation
import scala.meta._
import scala.collection.immutable.Seq

class enum(mixins: mixins = sys.error("wither"), debug: Boolean = false) extends StaticAnnotation {
  sys.error("Should not be instantiated directly")

  inline def apply(defn: Any): Any = meta {
      import enum._

      val q"new $_(..$args)" = this

    val mixins = args.flatMap {
      case arg"mixins[..$types]" => types.map {
        case Type.Name(text) => text
      }
      case _ => Seq()
    }

    val debug = args.collectFirst {
      case arg"debug = true" => true
    }.getOrElse(false)

    val result = defn match {
      case CompanionAndStuff(companion, maybeClass) =>
        val cls = maybeClass.collect {
          case TraitOrClass(defn) => defn.fold(expandClass, expandTrait)
        }.getOrElse(expandTrait(q"""sealed trait ${Type.Name(companion.name.value)}"""))

        q"""
           $cls
           ${expandCompanion(companion, mixins)}
         """

      case obj: Defn.Object =>
        expandCompanion(obj, mixins)

      case _ =>
        abort("Unsupported target for @enum annotation")
    }

    if (debug) println(result)
    result
  }
}

object enum {
  object CompanionAndStuff {
    def unapply(a: Any): Option[(Defn.Object, Option[Defn])] = a match {
      case Term.Block(Seq(cls: Defn, companion: Defn.Object)) => Some((companion, Some(cls)))
      case obj: Defn.Object => Some((obj, None))
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

  def seal(seq: Seq[Mod]): Seq[Mod] =
    if (seq.exists(_.is[Mod.Sealed])) seq
    else mod"sealed" +: seq

  def expandClass(cls: Defn.Class): Defn.Class = cls match {
    case q"..$mods class $name[..$tp] ..$ctorMods (...$paramss) extends $cs with $thing" =>
      q"..${seal(mods)} class $name[..$tp] ..$ctorMods (...$paramss) extends $cs with EnumEntry with $thing"

    case q"..$mods class $name[..$tp] ..$ctorMods (...$paramss)" =>
      q"..${seal(mods)} class $name[..$tp] ..$ctorMods (...$paramss) extends EnumEntry"
  }


  def expandTrait(cls: Defn.Trait): Defn.Trait = cls match {
    case q"..$mods trait $name extends ..$others" =>
      q"..${seal(mods)} trait $name extends EnumEntry with ..$others"
  }

  def mixinify(name: String)(typeConstructor: String): Ctor.Call = {
    ctor"${Ctor.Ref.Name(typeConstructor)}[${Type.Name(name)}]"
  }

  def expandStat(myName: String)(st: Stat): Seq[Stat] = st match {
    case q"..$mods val ..$terms = Value(...$paramss)" =>
      terms.map { case Pat.Var.Term(name) =>
        q"..$mods case object $name extends ${Ctor.Ref.Name(myName)}(...$paramss) {}"
      }

    case _ => Seq(st)
  }


  def expandCompanion(companion: Defn.Object, mixins: Seq[String]): Defn.Object = {
    val myName = companion.name.value
    val allMixins = ("Enum" +: mixins).map(mixinify(myName))
    companion match {
      case q"""
        ..$mods object $name extends ..$stuff {
          ..$stats
        }""" =>
          q"""
             ..$mods object $name extends ..$stuff with ..$allMixins {
              ..${stats.flatMap(expandStat(myName))}

              ${q"val values = findValues"}
             }
           """
    }
  }
}
