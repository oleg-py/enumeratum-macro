package com.olegpy.enumdslimpl

import scala.meta._


object expandValues {

  private def toTraits(tpe: Type): Seq[Ctor.Call] = tpe match {
    case Type.With(left, right) =>
      toTraits(left) ++ toTraits(right)
    case _: Type.Function =>
      abort("Value.mix with arrows is not supported. Use FunctionX types")
    case _ => Seq(tpe.ctorRef(Ctor.Name("Null")))
  }

  def apply(enumName: String, companion: Defn.Object, values: Seq[Stat]): Defn.Object = {
    val superclassType = Type.Name(enumName)
    val superclass = superclassType.ctorRef(Ctor.Name("Null"))
    val enumEntries = values flatMap {
      case q"""..$mods val ..$terms = Value(...$args)""" =>
        terms.map { case Pat.Var.Term(name) =>
            q"""..$mods case object $name extends $superclass(...$args) {}"""
        }

      case q"""..$mods val ..$terms = Value.mix[$tpe](...$args)""" =>
        val supertraits = toTraits(tpe)
        terms.map { case Pat.Var.Term(name) =>
          q"""..$mods case object $name extends $superclass(...$args) with ..$supertraits {}"""
        }
    }

    val findValues = q"val values = findValues"

    companion match {
      case q"..$mods object $name { ..$stats }" =>

        q"""..$mods object $name extends _root_.enumeratum.Enum[$superclassType] {
           ..${enumEntries ++ stats :+ findValues}
         }
         """

      case q"..$mods object $name extends $thing with ..$others { ..$stats }" =>
        q"""
          ..$mods object $name extends $thing with _root_.enumeratum.Enum[$superclassType] with ..$others {
           ..${enumEntries ++ stats :+ findValues}
         }
         """
    }
  }
}
