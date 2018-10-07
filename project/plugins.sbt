scalaVersion := "2.12.7"

scalacOptions ++= Seq("-unchecked", "-deprecation")

classpathTypes += "maven-plugin"

resolvers ++= Seq(
  Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins"))(Resolver.ivyStylePatterns),
  Resolver.bintrayRepo("malliina", "maven")
)

Seq(
  "com.malliina" %% "sbt-play" % "1.4.0",
  "com.malliina" % "sbt-filetree" % "0.2.1",
  "com.typesafe.sbt" % "sbt-gzip" % "1.0.2",
  "com.typesafe.sbt" % "sbt-digest" % "1.1.4",
  "com.typesafe.sbt" % "sbt-less" % "1.1.2"
) map addSbtPlugin

dependencyOverrides ++= Seq(
  "io.netty" % "netty" % "3.10.6.Final",
  "org.webjars" % "webjars-locator-core" % "0.33",
  "org.codehaus.plexus" % "plexus-utils" % "3.0.17",
  "com.google.guava" % "guava" % "23.0"
)
