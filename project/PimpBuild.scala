import java.nio.file.Paths

import com.mle.sbt.cloud._
import com.mle.sbtplay.PlayProjects
import sbt.Keys._
import sbt._
import sbtbuildinfo.Plugin._

object PimpBuild extends Build {
  lazy val pimpWeb = PlayProjects.playProject("pimpweb").settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    version := "1.3.7",
    scalaVersion := "2.11.2",
    libraryDependencies ++= deps,
    retrieveManaged := false,
    fork in Test := true
  )
  lazy val pimpSettings = commonSettings ++
    herokuSettings ++
    buildMetaSettings ++
    net.virtualvoid.sbt.graph.Plugin.graphSettings

  def herokuSettings = HerokuPlugin.settings ++ Seq(
    HerokuKeys.heroku := Paths.get( """C:\Program Files (x86)\Heroku\bin\heroku.bat""")
  )

  def buildMetaSettings = buildInfoSettings ++ Seq(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.mle.pimpweb"
  )

  val mleGroup = "com.github.malliina"

  lazy val deps = Seq(
    mleGroup %% "util" % "1.4.2",
    mleGroup %% "util-azure" % "1.3.1",
    mleGroup %% "play-base" % "0.1.0",
    "com.newrelic.agent.java" % "newrelic-agent" % "2.15.1" % "provided"
  )
}