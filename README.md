# enumeratum-macro
A pleasant syntax for [enumeratum] with decent Intellij IDEA support

## Quick start
Only Scala 2.12 is currently supported

Add this to your build definition:
```sbt
resolvers += "JitPack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "com.beachape" %% "enumeratum" % "1.5.13",
  "org.scalameta" %% "scalameta" % "1.8.0" % Provided,
  "com.github.olegpy" % "enumeratum-macro" % "0.0.1"
)
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.patch)
```

Define a enum:

```scala
import com.olegpy.enumdsl._

@enum trait Greeting {
  val Hello, GoodBye, Hi, Bye = Value
}
```

That's it!

## More Examples
#### Manual name override / constructor parameters
Translated example from enumeratum readme:

```scala
import com.olegpy.enumdsl._
@enum class State(override val entryName: String) {
    val Alabama = Value("AL")
    val Alaska = Value("AK")
   // and so on and so forth.
}
```

Named parameters are supported, but might produce spurous highlighting
errors in IDEA (see note below on IDEA support):

```scala
import com.olegpy.enumdsl._
@enum class State(override val entryName: String) {
    val Alabama = Value("AL")
    val Alaska = Value(entryName = "AK")
   // and so on and so forth.
}
```

#### Value mixins (e.g. to override the name)
Translated example from enumeratum readme:
```scala
import com.olegpy.enumdsl._
import enumeratum.EnumEntry._

@enum trait Greeting extends Snakecase {
  val Hello, GoodBye = Value
  val ShoutGoodBye = Value.mix[Uppercase]
}
```

Any `trait` can be a mixin. However, the resulting type must not
have unimplemented methods.

Multiple mixins are supported using familiar `with` syntax:

```scala
trait MyMixin {
  def superMethod() = println() // suppose it does something awesome
}

import com.olegpy.enumdsl._
import enumeratum.EnumEntry._

@enum trait Greeting extends Snakecase {
  val Hello, GoodBye = Value
  val ShoutGoodBye = Value.mix[Camelcase with Uppercase with MyMixin]
}
```

#### Companion mixins
If you're using `enumeratum-quill` and `enumeratum-circe`:

```scala
import enumeratum._

@enum(mix[QuillEnum, CirceEnum])
trait State {
  val On, Off = Value
}
```

Custom mixins are supported, so you can keep things DRY:

```scala
import enumeratum._

trait EnumUtils[A <: EnumEntry] extends QuillEnum[A] with CirceEnum[A] {
  this: Enum[A] =>

  def withNameEither(name: String): Either[String, A] =
    withNameOption(s).toRight(s"Not found: $s")
}

@enum(mix[EnumUtils])
trait State {
  val On, Off = Value
}

```

`mix` magic method supports up to 5 mixins. If you need more, either
define a custom mixin or use `mix` multiple times (this will cause
highlighting errors in IDEA, so it's not recommended):

```@enum(
  mix[EnumUtils1, EnumUtils2, EnumUtils3, EnumUtils4, EnumUtils5],
  mix[CirceEnum]
) trait State {
  val On, Off = Value
}
```

## Goals
#### Provide lightweight way to define a [enumeratum] Enum
Enums from enumeratum are great. They provide a lot of integrations
(Quill, Circe, pureconfig) and a lot of methods to perform a lookup
depending on your needs (e.g. `withNameInsensitiveOption`). The library
has been actively supported for a while already.

However, there's one pain point with these which enumeratum does not
solve: syntax. A lot of boilerplate is involved in defining a simple
enum. Syntactically, enumeratum is *way* inferior to
`scala.Enumeration`, Java enums, or even defining a sealed trait / case
object manually (if all you want is pattern-matching with exhaustivity).

Other libraries like [numerato] have emerged to solve syntax problems,
yet they don't have that much support in the ecosystem, which might
pose some migration cost.

This problem is solved by only providing syntax for existing library.
Syntax provided here aims to support most use-cases, allowing falling
back to full-fledged manual `Enum` implementation where not supported
or if there are any issues with `@enum` macro.

#### Be easy to add to existing project
It should be easy to use for people who are using `enumeratum` already:

- You can refactor existing code one enum at a time, or only use it for
  new types.
- It does not depend on a particular version of enumeratum, being
  functional for older versions (starting at 1.3.2)

Syntax also aims to be compatible with [numerato] and
`scala.Enumeration`, and for simple use cases porting is supposed to be
straightforward (feel free to open a ticket if it's not for your
use-case).

#### Be easy to remove from a project
For various reasons you might end up stopping using this library:

- You find provided syntax too limited to your use case
- You decide macro annotations is too much magic for you
- This library becomes unmaintained in some unknown future

Because of such, falling back to [enumeratum] directly is as simple as:

- use `@enum(debug = true)` to see printed trees and replace `@enum`
  annotated class with code that will be printed by a compiler
  - *or* (IDEA only) expand macro annotation using sidebar button
- remove this library from dependencies

In doing so, you keep all benefits of enumeratum. All you lose is
some syntax sugar.

If you only want exhaustivity warnings on pattern matches, it is
possible to remove enumeratum completely and use plain `sealed trait` /
`case object`s. For that, just delete all enumeratum-specific
superclasses (`Enum`, `EnumEntry`) and `val values = findValues` method.

For simple enums, it's possible to use `scala.Enumeration` or [numerato]
instead, because syntax of `@enum` macro aligns closely to these.
Advanced features such as mixins do not exist on those, however.

#### Support Intellij IDEA
Enum libraries built on scala macros tend to not play well with IDEA,
which is the [most popular][jb-research] IDE for Scala.

`enumeratum-macro` adresses this problem by using [scalameta], which is
supported quite well. All advanced features are designed with syntactic
compatibility in mind, so a lot of things like go to definition just
work.

Unfortunately, it's not possible to support ALL syntactic features
without spurous red squigglies. Where possible, `enumeratum-macro` does
its best to localize such errors to the definition site of `@enum`
class, where they can be easily suppressed.

**NOTE:** For best support, define `@enum`s in separate files from where
you access values.

**NOTE 2:** For IDEA, some warnings can be suppressed using magic
comments (make a face at your IDE):

```
/*_*/
// Type-aware highlighting is disabled below
@enum class Foo(number: Int) {
  val Bar = Value(number = 42) // named arguments won't appear red
}
/*_*/

// Type-aware highlighting is enabled below
```

This trick works with other cases where not-so-supported macros are
involved, e.g. [shapeless]. Keep it in your arsenal, fellow Intellij
user.

## What it does, actually

```scala
@enum(mix[Mixin1, Mixin2]) class Sample (number: Int = 0) {
  val Foo, Bar = Value
  val Baz = Value(number = 0)
  val Qux = Value.mix[ValueMixin1 with ValueMixin2](11)

  def method = println(number)
}
```

- Class / trait is made sealed and abstract
- Class is made to extend `enumeratum.EnumEntry`
- All calls to `Value` and `Value.mix` are removed

```scala
sealed abstract class Sample (number: Int = 0)
  extends enumeratum.EnumEntry
{
  def method = println(number)
}
```

- Companion is made to extend `enumeratum.Enum[/*class name*/]` and
  all type constructors passed to `mix`

```scala
object Sample extends enumeratum.Enum[Sample]
  with Mixin1[Sample]
  with Mixin2[Sample]
```

- All previously removed calls to `Value` and `Value.mix` become newly
  defined `case object`s
  - All arguments are passed in as-is, including named ones
  - All traits provided in `Value.mix` type parameters are mixed into
    relevant object (again, as-is).

- Field `val values = findValues` is defined

```scala
{
    case object Foo extends Sample
    case object Bar extends Sample(number = 0)
    case object Qux extends Sample(11) with ValueMixin1 with ValueMixin2

    val values = findValues
}
```

## License
MIT


  [scalameta]: http://scalameta.org/
  [shapeless]: https://github.com/milessabin/shapeless/
  [enumeratum]: https://github.com/lloydmeta/enumeratum
  [numerato]: https://github.com/maxaf/numerato
  [jb-research]: https://www.jetbrains.com/research/devecosystem-2017/scala/