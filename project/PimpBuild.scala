import com.malliina.sbtplay.PlayProject
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}
import sbtbuildinfo.BuildInfoPlugin

object PimpBuild {
  lazy val pimpWeb = PlayProject("pimpweb")
    .enablePlugins(BuildInfoPlugin)
    .settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    version := "1.6.3",
    scalaVersion := "2.11.8",
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("malliina", "maven")
    ),
    libraryDependencies ++= deps,
    retrieveManaged := false,
    fork in Test := true
  )

  lazy val pimpSettings = commonSettings ++ buildMetaSettings

  def buildMetaSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.malliina.pimpweb"
  )

  val malliinaGroup = "com.malliina"

  lazy val deps = Seq(
    malliinaGroup %% "util-azure" % "2.1.0",
    malliinaGroup %% "play-base" % "2.8.0",
    "org.pegdown" % "pegdown" % "1.6.0",
    "com.newrelic.agent.java" % "newrelic-agent" % "2.15.1" % "provided",
    "com.lihaoyi" %% "scalatags" % "0.6.2"
  )
}
