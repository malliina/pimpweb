import sbt._
import sbt.Keys._

object WebBuildBuild extends Build {
  // "build.sbt" goes here
  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.10.6",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers ++= Seq(
      "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
      Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins"))(Resolver.ivyStylePatterns)
    )
  ) ++ sbtPlugins

  def sbtPlugins = Seq(
    "com.typesafe.play" % "sbt-plugin" % "2.5.3",
    "com.malliina" %% "sbt-play" % "0.7.2",
    "com.eed3si9n" % "sbt-buildinfo" % "0.5.0"
  ) map addSbtPlugin

  override lazy val projects = Seq(root)
  lazy val root = Project("plugins", file("."))
}
