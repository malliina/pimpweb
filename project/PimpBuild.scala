import java.nio.file.Paths

import com.mle.sbt.cloud._
import com.mle.sbtplay.PlayProjects
import com.mle.sbtutils.SbtProjects
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}
import sbtbuildinfo.BuildInfoPlugin

object PimpBuild extends Build {
  lazy val pimpWeb = SbtProjects.testableProject("pimpweb")
    .enablePlugins(BuildInfoPlugin, play.sbt.PlayScala).settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    version := "1.4.3",
    scalaVersion := "2.11.6",
    libraryDependencies ++= deps,
    retrieveManaged := false,
    fork in Test := true
  )
  lazy val pimpSettings = commonSettings ++ herokuSettings ++ buildMetaSettings

  def herokuSettings = HerokuPlugin.settings ++ Seq(
    HerokuKeys.heroku := Paths.get( """C:\Program Files (x86)\Heroku\bin\heroku.bat""")
  )

  def buildMetaSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.mle.pimpweb"
  )

  val mleGroup = "com.github.malliina"

  lazy val deps = Seq(
    mleGroup %% "util-azure" % "1.9.0",
    mleGroup %% "play-base" % "0.5.0",
    "com.newrelic.agent.java" % "newrelic-agent" % "2.15.1" % "provided"
  )
}
