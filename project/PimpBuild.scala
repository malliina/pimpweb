import java.nio.file.Paths

import com.mle.sbt.cloud._
import com.mle.sbtplay.PlayProjects
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}
import sbtbuildinfo.BuildInfoPlugin

object PimpBuild extends Build {
  lazy val pimpWeb = PlayProjects.plainPlayProject("pimpweb").enablePlugins(BuildInfoPlugin).settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    version := "1.4.1",
    scalaVersion := "2.11.6",
    libraryDependencies ++= deps,
    retrieveManaged := false,
    fork in Test := true,
    resolvers ++= Seq(
      "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
      sbt.Resolver.jcenterRepo,
      "Bintray malliina" at "http://dl.bintray.com/malliina/maven")
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
    mleGroup %% "util-azure" % "1.8.1",
    mleGroup %% "play-base" % "0.4.1",
    "com.newrelic.agent.java" % "newrelic-agent" % "2.15.1" % "provided"
  )
}
