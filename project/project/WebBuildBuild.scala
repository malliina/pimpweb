import sbt._
import sbt.Keys._

/**
 *
 * @author mle
 */
object WebBuildBuild extends Build {
  // "build.sbt" goes here
  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.9.2",
    resolvers ++= Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Typesafe ivy releases" at "http://repo.typesafe.com/typesafe/ivy-releases/",
      "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"),
    scalacOptions ++= Seq("-unchecked", "-deprecation")
  ) ++ sbtPlugins

  def sbtPlugins = Seq(
    "play" % "sbt-plugin" % "2.1.4",
    "com.github.malliina" %% "sbt-paas-deployer" % "0.102",
    "com.github.mpeltonen" % "sbt-idea" % "1.5.1",
    "com.eed3si9n" % "sbt-buildinfo" % "0.2.5",
    "com.timushev.sbt" % "sbt-updates" % "0.1.2"
  ) map addSbtPlugin

  override lazy val projects = Seq(root)
  lazy val root = Project("plugins", file("."))
}

