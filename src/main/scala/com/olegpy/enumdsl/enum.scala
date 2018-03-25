package com.olegpy
package enumdsl

import scala.annotation.StaticAnnotation
import scala.meta._

import com.olegpy.enumdslimpl.generateEnum

/*_*/
class enum(mixins: mix = mix, debug: Boolean = false) extends StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    val q"new $_(..$args)" = this
    generateEnum(args, defn)
  }
}