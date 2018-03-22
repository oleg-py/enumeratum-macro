name := "enumeratum-macro"

inThisBuild(Seq(
  version := "0.0.1",
  organization := "com.olegpy",
  scalaVersion := "2.12.3"
))

libraryDependencies ++= Seq(
  "org.scalameta" %% "scalameta" % "1.8.0" % Provided,
  "org.scalameta" %% "testkit"   % "1.8.0" % Test,
  "com.lihaoyi"   %% "utest"     % "0.5.3" % Test,
  "com.beachape" %% "enumeratum" % "1.5.13" % Test,
  "com.beachape" %% "enumeratum-quill" % "1.5.13" % Test
)

addCompilerPlugin(
  "org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full
)

scalacOptions += "-Xplugin-require:macroparadise"

testFrameworks += new TestFramework("utest.runner.Framework")

// WORKAROUND https://github.com/scalameta/paradise/issues/10
scalacOptions in (Compile, console) ~= (_ filterNot (_ contains "paradise"))
// WORKAROUND https://github.com/scalameta/paradise/issues/216
sources in (Compile, doc) := Nil