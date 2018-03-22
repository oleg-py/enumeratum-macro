package com.olegpy.enumeratum

import scala.annotation.compileTimeOnly


@compileTimeOnly("Value is a part of @enum DSL and should not be used directly")
object Value {
   def apply(args: Any*): Nothing = sys.error("")
}
