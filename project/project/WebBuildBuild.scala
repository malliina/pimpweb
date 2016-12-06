import sbt._
import sbt.Keys._

object WebBuildBuild {
  // "build.sbt" goes here
  val settings = Seq(
    scalaVersion := "2.10.6",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers ++= Seq(
      "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
      Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins"))(Resolver.ivyStylePatterns)
    )
  ) ++ sbtPlugins

  def sbtPlugins = Seq(
    "com.malliina" %% "sbt-play" % "0.7.2",
    "com.eed3si9n" % "sbt-buildinfo" % "0.5.0"
  ) map addSbtPlugin
}
