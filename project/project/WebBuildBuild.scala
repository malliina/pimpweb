import sbt._
import sbt.Keys._

/**
 *
 * @author mle
 */
object WebBuildBuild extends Build {
  // "build.sbt" goes here
  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.10.3",
    resolvers ++= Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Typesafe ivy releases" at "http://repo.typesafe.com/typesafe/ivy-releases/",
      "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"),
    scalacOptions ++= Seq("-unchecked", "-deprecation")
  ) ++ sbtPlugins

  def sbtPlugins = Seq(
    "com.typesafe.play" % "sbt-plugin" % "2.2.1",
    "com.github.malliina" %% "sbt-paas-deployer" % "1.0.0",
    "com.github.mpeltonen" % "sbt-idea" % "1.5.1",
    "com.eed3si9n" % "sbt-buildinfo" % "0.3.0",
    "com.timushev.sbt" % "sbt-updates" % "0.1.2",
    "net.virtual-void" % "sbt-dependency-graph" % "0.7.4"
  ) map addSbtPlugin

  override lazy val projects = Seq(root)
  lazy val root = Project("plugins", file("."))
}

