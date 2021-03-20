val buildRoot = (project in file("."))
  .settings(
    scalaVersion := "2.12.12",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.9.2",
      "com.malliina" %% "primitives" % "1.17.0"
    ),
    Seq(
      "com.malliina" %% "sbt-nodejs" % "1.0.0",
      "com.github.gseitz" % "sbt-release" % "1.0.11",
      "com.eed3si9n" % "sbt-buildinfo" % "0.10.0",
      "org.scala-js" % "sbt-scalajs" % "1.5.0",
      "org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0",
      "ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0",
      "ch.epfl.scala" % "sbt-bloop" % "1.4.8",
      "org.scalameta" % "sbt-scalafmt" % "2.4.2"
    ) map addSbtPlugin
  )
