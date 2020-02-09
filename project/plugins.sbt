val buildRoot = (project in file("."))
  .settings(
    scalaVersion := "2.12.10",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.8.1",
      "com.malliina" %% "primitives" % "1.13.0"
    ),
    Seq(
      "com.malliina" %% "sbt-nodejs" % "0.15.7",
      "com.github.gseitz" % "sbt-release" % "1.0.11",
      "com.eed3si9n" % "sbt-buildinfo" % "0.9.0",
      "com.typesafe.sbt" % "sbt-gzip" % "1.0.2",
      "com.typesafe.sbt" % "sbt-digest" % "1.1.4",
      "com.vmunier" % "sbt-web-scalajs" % "1.0.10-0.6",
      "org.scala-js" % "sbt-scalajs" % "0.6.32",
      "org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0",
      "ch.epfl.scala" % "sbt-web-scalajs-bundler-sjs06" % "0.16.0",
      "com.lihaoyi" % "workbench" % "0.4.1",
      "ch.epfl.scala" % "sbt-bloop" % "1.3.4",
      "org.scalameta" % "sbt-scalafmt" % "2.3.0"
    ) map addSbtPlugin
  )
