package com.olegpy

import scala.reflect.{ClassTag, classTag}


package object enumdsl {
  implicit class Assertions[A <: AnyRef](a: A) {
    def extends_[B: ClassTag]: Boolean =
      classTag[B].runtimeClass.isInstance(a)
  }
}
