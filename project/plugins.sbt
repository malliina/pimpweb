scalaVersion := "2.10.6"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers ++= Seq(
  Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins"))(Resolver.ivyStylePatterns),
  Resolver.bintrayRepo("malliina", "maven")
)

Seq(
  "com.malliina" %% "sbt-play" % "0.9.7",
  "com.typesafe.sbt" % "sbt-gzip" % "1.0.0",
  "com.typesafe.sbt" % "sbt-digest" % "1.1.1"
) map addSbtPlugin
