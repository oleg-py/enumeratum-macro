package com.olegpy
package enumdsl

import enumdslimpl._
import scala.annotation.compileTimeOnly


@compileTimeOnly("Should only be used in @enum definition")
object Value {
  @compileTimeOnly("Should only be used in @enum definition")
  def apply(args: Any*): Nothing = evicted
  @compileTimeOnly("Should only be used in @enum definition")
  def mix[S]: Nothing = evicted
  @compileTimeOnly("Should only be used in @enum definition")
  def mix[S](args: Any*): Nothing = evicted
}
