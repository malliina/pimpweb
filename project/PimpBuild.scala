import java.nio.file.Paths
import sbt._
import sbt.Keys._
import com.mle.sbt.cloud._
import sbtbuildinfo.Plugin._

object PimpBuild extends Build {

  import Dependencies._

  lazy val pimpWeb = play.Project("pimpweb",
    path = file("."),
    applicationVersion = "1.0",
    dependencies = Seq(utilDep, utilAzure, scalaTest, cloudFoundryJpa, newRelic),
    settings = pimpSettings
  )

  // Hack for play compat
  override def settings = super.settings ++ com.typesafe.sbtidea.SbtIdeaPlugin.ideaSettings

  val commonSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.10.2",
    retrieveManaged := false,
    sbt.Keys.fork in Test := true,
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  )
  val pimpSettings = commonSettings ++
    HerokuPlugin.settings ++
    AppFogPlugin.settings ++
    CloudFoundryPlugin.settings ++ Seq(
    CloudFoundryBasedKeys.packagedApp <<= play.Project.dist map (_.toPath),
    CloudFoundryKeys.framework in CloudFoundryKeys.CloudFoundry := PlayFramework,
    HerokuKeys.heroku := Paths.get( """C:\Program Files (x86)\Heroku\bin\heroku.bat""")
  ) ++ buildInfoSettings ++ Seq(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.mle.pimpweb"
  )
}

object Dependencies {
  val utilVersion = "1.0.0"
  val oldUtilVersion = "0.7.1"
  val utilGroup = "com.github.malliina"
  val utilDep = utilGroup %% "util" % utilVersion
  val oldUtilDep = utilGroup %% "util" % oldUtilVersion
//  val utilRmi = utilGroup %% "util-rmi" % oldUtilVersion
  val utilAzure = utilGroup %% "util-azure" % utilVersion
  val scalaTest = "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  val jodaTime = "joda-time" % "joda-time" % "2.1"
  val jodaConvert = "org.joda" % "joda-convert" % "1.3"
  val cloudFoundryJpa = "play" %% "play-java-jpa" % "2.1.0"
  val newRelic = "com.newrelic.agent.java" % "newrelic-agent" % "2.15.1" % "provided"
}