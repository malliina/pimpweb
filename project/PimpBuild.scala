import java.nio.file.Paths

//import com.malliina.sbt.cloud._
import com.malliina.sbtplay.PlayProject
import com.malliina.sbtutils.SbtProjects
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}
import sbtbuildinfo.BuildInfoPlugin

object PimpBuild extends Build {
  lazy val pimpWeb = SbtProjects.testableProject("pimpweb")
    .enablePlugins(BuildInfoPlugin, play.sbt.PlayScala).settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    version := "1.5.0",
    scalaVersion := "2.11.7",
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("malliina", "maven")
    ),
    libraryDependencies ++= deps,
    retrieveManaged := false,
    fork in Test := true
  )
  lazy val pimpSettings = commonSettings ++ buildMetaSettings

//  def herokuSettings = HerokuPlugin.settings ++ Seq(
//    HerokuKeys.heroku := Paths.get( """C:\Program Files (x86)\Heroku\bin\heroku.bat""")
//  )

  def buildMetaSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.mle.pimpweb"
  )

  val mleGroup = "com.malliina"

  lazy val deps = Seq(
    mleGroup %% "util-azure" % "2.1.0",
    mleGroup %% "play-base" % "2.5.0",
    "com.newrelic.agent.java" % "newrelic-agent" % "2.15.1" % "provided"
  )
}
