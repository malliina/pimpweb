scalaVersion := "2.12.14"
scalacOptions ++= Seq("-unchecked", "-deprecation")
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "com.malliina" %% "primitives" % "1.17.0"
)
Seq(
  "com.malliina" %% "sbt-nodejs" % "1.2.3",
  "com.github.gseitz" % "sbt-release" % "1.0.13",
  "com.eed3si9n" % "sbt-buildinfo" % "0.10.0",
  "org.scala-js" % "sbt-scalajs" % "1.6.0",
  "org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0",
  "ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0",
  "org.scalameta" % "sbt-scalafmt" % "2.4.2"
) map addSbtPlugin
