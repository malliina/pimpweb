import sbt._
import sbt.Keys._

/**
 *
 * @author mle
 */
object WebBuildBuild extends Build {
  // "build.sbt" goes here
  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
  ) ++ sbtPlugins

  def sbtPlugins = Seq(
    "com.typesafe.play" % "sbt-plugin" % "2.3.8",
    "com.github.malliina" %% "sbt-paas-deployer" % "1.0.0",
    "com.github.malliina" %% "sbt-play" % "0.1.1",
    "com.eed3si9n" % "sbt-buildinfo" % "0.3.0"
  ) map addSbtPlugin

  override lazy val projects = Seq(root)
  lazy val root = Project("plugins", file("."))
}
