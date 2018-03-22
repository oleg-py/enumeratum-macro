package com.olegpy.enumeratum

import scala.annotation.compileTimeOnly

sealed trait mixins

@compileTimeOnly("mixins is a part of enumeratum DSL only supported inside @enum")
object mixins {
  trait A; trait B; trait C; trait D; trait E
  def apply[a[_]](implicit a: A): mixins =
    sys.error("impossible")

  def apply[a[_], b[_]](implicit b: B): mixins =
    sys.error("impossible")

  def apply[a[_], b[_], c[_]](implicit c: C): mixins =
    sys.error("impossible")

  def apply[a[_], b[_], c[_], d[_]](implicit d: D): mixins =
    sys.error("impossible")

  def apply[a[_], b[_], c[_], d[_], e[_]](implicit e: E): mixins =
    sys.error("impossible")
}
