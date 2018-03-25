package com.olegpy.enumdsl


import utest._

object EnumMixins extends TestSuite {
  val tests = Tests {
    "Single mixin" - {
      trait MyMixin[Enum]

      @enum(mix[MyMixin]) class Foo {
        val Bar = Value
      }

      assert(Foo.extends_[MyMixin[_]])
    }

    "Multiple mixins" - {
      trait ValMixin
      trait MyMixin[Enum]
      trait MyMixin2[Enum]

      @enum(mix[MyMixin, MyMixin2]) class Foo {
        val Bar = Value.mix[ValMixin]
      }

      assert(Foo.extends_[MyMixin[_]])
      assert(Foo.extends_[MyMixin2[_]])
      assert(Foo.Bar.extends_[ValMixin])
    }

    "Multiple mixin appication" - {
      trait MyMixin[Enum]
      trait MyMixin2[Enum]

      @enum(mix[MyMixin], mix[MyMixin2]) trait Foo {
        val Bar = Value
      }

      assert(Foo.extends_[MyMixin[_]])
      assert(Foo.extends_[MyMixin2[_]])
    }
  }
}