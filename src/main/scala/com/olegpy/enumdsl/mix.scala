package com.olegpy
package enumdsl

import enumdslimpl._
import scala.annotation.compileTimeOnly
import scala.language.higherKinds


sealed trait mix

/**
  * Syntax support for defining enums with mixins, i.e.
  *
  * `&#064;enum(mix[QuillEnum, CirceEnum]) trait Foo`
  *
  */
object mix extends mix {
  @compileTimeOnly("Part of @enum DSL that should not be used directly")
  def apply[M1[_]](implicit sig: T1): mix = evicted

  @compileTimeOnly("Part of @enum DSL that should not be used directly")
  def apply[M1[_], M2[_]](implicit sig: T2): mix = evicted

  @compileTimeOnly("Part of @enum DSL that should not be used directly")
  def apply[M1[_], M2[_], M3[_]](implicit sig: T3): mix = evicted

  @compileTimeOnly("Part of @enum DSL that should not be used directly")
  def apply[M1[_], M2[_], M3[_], M4[_]](implicit sig: T4): mix = evicted

  @compileTimeOnly("Part of @enum DSL that should not be used directly")
  def apply[M1[_], M2[_], M3[_], M4[_], M5[_]](implicit sig: T5): mix = evicted

  // Dummies used to give mix[Mixin] different erased signatures
  // Which is required for scalac to compile above definitions
  trait T1; trait T2; trait T3; trait T4; trait T5
}