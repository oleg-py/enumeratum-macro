package com.olegpy.enumdsl

import enumeratum.EnumEntry._
import enumeratum._
import utest._


object SimpleEnums extends TestSuite {
  val tests = Tests {
    "Simple traits" - {
      @enum trait Greetings {
        val Hello, Hola = Value
      }
      assert(Greetings.extends_[Enum[Greetings]])
      for (value <- Seq(Greetings.Hello, Greetings.Hola)) {
        assert(value.extends_[EnumEntry])
        assert(value.extends_[Greetings])
      }
    }

    "abstract classes" - {
      @enum abstract class Greetings {
        val Hello, Hola = Value
      }
      assert(Greetings.extends_[Enum[Greetings]])
      for (value <- Seq(Greetings.Hello, Greetings.Hola)) {
        assert(value.extends_[EnumEntry])
        assert(value.extends_[Greetings])
      }
    }

    "abstract classes, parameters and parents" - {
      trait Marker
      abstract class Parent(val stored: String)
      @enum abstract class Greetings(val num: Int) extends Parent("ok") with Marker {
        val Hello, Hola = Value(42)
        val Sup = Value(num = 11)
      }

      assert(Greetings.Hello.stored == "ok")
      assert(Greetings.Hola.num == 42)
      assert(Greetings.Sup.num == 11)
      assert(Greetings.Hola.extends_[Marker])
      assert(Greetings.Sup.extends_[Parent])
    }

    "naming strategies, multiple values, methods" - {
      trait Marker
      @enum trait Word extends Lowercase {
        val Eggs, Ham = Value
        def whatAmI = "I am " + entryName
        val Spam = Value.mix[Uppercase with Marker]
      }

      assert(Word.Eggs.whatAmI == "I am eggs")
      assert(Word.Spam.whatAmI == "I am SPAM")
      assert(Word.Spam.extends_[Marker])
      assert(!Word.Ham.extends_[Marker])
      assert(!Word.Eggs.extends_[Marker])

      @enum class WithParam(val i: Int) {
        val Test = Value.mix[Marker](42)
      }

      assert(WithParam.Test.i == 42)
      assert(WithParam.Test.extends_[Marker])
    }

    "explicit companions" - {
      @enum trait Foo { val X, Y, Z = Value }
      object Foo {
        def number = 42
      }
      assert(Foo.number == 42)
    }
  }
}
