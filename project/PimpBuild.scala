import java.nio.file.Paths
import sbt._
import sbt.Keys._
import com.mle.sbt.cloud._
import sbtbuildinfo.Plugin._

object PimpBuild extends Build {
  lazy val pimpWeb = Project("pimpweb", file(".")).settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    version := "1.3.0",
    scalaVersion := "2.10.3",
    libraryDependencies ++= deps,
    retrieveManaged := false,
    fork in Test := true,
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  )
  lazy val pimpSettings = commonSettings ++
    herokuSettings ++
    buildMetaSettings ++
    net.virtualvoid.sbt.graph.Plugin.graphSettings ++
    play.Project.playScalaSettings

  def herokuSettings = HerokuPlugin.settings ++ Seq(
    HerokuKeys.heroku := Paths.get( """C:\Program Files (x86)\Heroku\bin\heroku.bat""")
  )

  def buildMetaSettings = buildInfoSettings ++ Seq(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.mle.pimpweb"
  )

  val utilVersion = "1.0.0"
  val utilGroup = "com.github.malliina"

  lazy val deps = Seq(
    utilGroup %% "util" % utilVersion,
    utilGroup %% "util-azure" % utilVersion,
    "org.scalatest" %% "scalatest" % "1.9.2" % "test",
    "com.newrelic.agent.java" % "newrelic-agent" % "2.15.1" % "provided"
  )
}