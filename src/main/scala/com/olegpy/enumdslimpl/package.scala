package com.olegpy

import scala.meta._

import com.olegpy.enumdslimpl.matchers._


package object enumdslimpl {
  //noinspection TypeAnnotation
  val  Seq    = scala.collection.immutable.Seq
  type Seq[A] = scala.collection.immutable.Seq[A]

  def evicted: Nothing =
    sys.error("Runtime application of @enum DSL is not possible")

  def generateEnum(args: Seq[Term.Arg], annotee: Any): Stat = {
    val isDebug = args.collectFirst {
      case arg"debug = true" => true
    }.getOrElse(false)

    val mixins = args.flatMap {
      case arg"mix[..$mixins]" => mixins
      case _ => Seq()
    }

    val transformed =
      annotee match {
        case FallbackToEmptyCompanion(TraitOrClass(toc), companion) =>
          val enumName = toc.fold(identity, identity).name.value
          val (toc2, values) = extractValues(enumify(toc))
          val companion2 =
            applyMixins(enumName, expandValues(enumName, companion, values), mixins)

          q"""
             ${toc2.fold(identity, identity)}
             $companion2
           """
        case _ => abort("@enum must annotate a trait or a class")
      }
    if (isDebug) println(transformed)
    transformed
  }
}
