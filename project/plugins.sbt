scalaVersion := "2.12.9"

scalacOptions ++= Seq("-unchecked", "-deprecation")

classpathTypes += "maven-plugin"

resolvers ++= Seq(
  Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins"))(Resolver.ivyStylePatterns),
  Resolver.bintrayRepo("malliina", "maven"),
  Resolver.jcenterRepo,
  Resolver.mavenCentral
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.7.4"
)

Seq(
  "com.malliina" %% "sbt-nodejs" % "0.14.0",
  "com.github.gseitz" % "sbt-release" % "1.0.11",
  "com.eed3si9n" % "sbt-buildinfo" % "0.9.0",
  "com.typesafe.sbt" % "sbt-gzip" % "1.0.2",
  "com.typesafe.sbt" % "sbt-digest" % "1.1.4",
  "com.typesafe.sbt" % "sbt-less" % "1.1.2",
  "com.vmunier" % "sbt-web-scalajs" % "1.0.6",
  "org.scala-js" % "sbt-scalajs" % "0.6.28",
  "org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0",
  "ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.14.0",
  "com.lihaoyi" % "workbench" % "0.4.1"
) map addSbtPlugin
