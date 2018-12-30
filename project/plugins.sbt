scalaVersion := "2.12.8"

scalacOptions ++= Seq("-unchecked", "-deprecation")

classpathTypes += "maven-plugin"

resolvers ++= Seq(
  Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins"))(Resolver.ivyStylePatterns),
  Resolver.bintrayRepo("malliina", "maven"),
  Resolver.jcenterRepo,
  Resolver.mavenCentral
)

Seq(
  "com.malliina" %% "sbt-play" % "1.4.1",
  "com.malliina" % "sbt-filetree" % "0.2.1",
  "com.typesafe.sbt" % "sbt-gzip" % "1.0.2",
  "com.typesafe.sbt" % "sbt-digest" % "1.1.4",
  "com.typesafe.sbt" % "sbt-less" % "1.1.2",
  "com.vmunier" % "sbt-web-scalajs" % "1.0.6",
  "org.scala-js" % "sbt-scalajs" % "0.6.26",
  "ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.14.0"
) map addSbtPlugin

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "scalatags" % "0.6.7",
  "com.malliina" %% "util-html" % "4.18.1"
)
