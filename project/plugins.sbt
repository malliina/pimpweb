scalaVersion := "2.12.4"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers ++= Seq(
  Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins"))(Resolver.ivyStylePatterns),
  Resolver.bintrayRepo("malliina", "maven")
)

Seq(
  "com.malliina" %% "sbt-play" % "1.2.2",
  "com.malliina" % "sbt-filetree" % "0.2.1",
  "com.typesafe.sbt" % "sbt-gzip" % "1.0.2",
  "com.typesafe.sbt" % "sbt-digest" % "1.1.4",
  "com.typesafe.sbt" % "sbt-less" % "1.1.2"
) map addSbtPlugin
