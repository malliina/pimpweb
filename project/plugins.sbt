val utilsVersion = "2.0.8"

libraryDependencies ++= Seq(
  "com.malliina" %% "primitives" % "6.16.0"
)
Seq(
  "com.malliina" % "sbt-utils-maven" % utilsVersion,
  "com.malliina" % "sbt-nodejs" % utilsVersion,
  "com.malliina" % "sbt-revolver-rollup" % utilsVersion,
  "com.malliina" %% "sbt-nodejs" % utilsVersion,
  "org.scala-js" % "sbt-scalajs" % "1.22.0",
  "org.portable-scala" % "sbt-scalajs-crossproject" % "1.4.0",
  "org.scalameta" % "sbt-scalafmt" % "2.6.2"
) map addSbtPlugin
