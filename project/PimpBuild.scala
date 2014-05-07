import com.mle.sbtutils.SbtUtils
import java.nio.file.Paths
import sbt._
import sbt.Keys._
import com.mle.sbt.cloud._
import sbtbuildinfo.Plugin._

object PimpBuild extends Build {
  lazy val pimpWeb = SbtUtils.testableProject("pimpweb").settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    version := "1.3.3",
    scalaVersion := "2.11.0",
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

  val utilVersion = "1.3.1"
  val myGroup = "com.github.malliina"

  lazy val deps = Seq(
    myGroup %% "util" % utilVersion,
    myGroup %% "util-azure" % utilVersion,
    myGroup %% "play-base" % "0.0.4",
    "com.newrelic.agent.java" % "newrelic-agent" % "2.15.1" % "provided"
  )
}