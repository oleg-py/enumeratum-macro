import enumeratum._
import com.olegpy.enumeratum._


object ct {
  @enum(mixins[QuillEnum]) object Meta {
    val A, B, C = Value
  }

  @enum abstract class Foo(s: String)
  object Foo {
    val A = Value("bar")
    val notValue = 42
    val B = Value(s = "foo")
  }

  @enum(debug = true) object Bar {
    val B, C, D = Value
  }
}
