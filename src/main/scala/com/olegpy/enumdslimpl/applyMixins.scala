package com.olegpy.enumdslimpl

import scala.meta._

object applyMixins {
  def apply(enumName: String, companion: Defn.Object, mixins: Seq[Type]): Defn.Object = {
    companion match {
      case q"..$mods object $name extends $first with ..$rest { ..$stats }" =>
        val typeds = mixins.map { tpe =>
          Type.Apply(tpe, Seq(Type.Name(enumName))).ctorRef(Ctor.Name("Null"))
        }
        q"""..$mods object $name extends $first with ..$typeds with ..$rest { ..$stats }"""
    }
  }
}
