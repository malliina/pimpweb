import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbt.unix.LinuxPlugin
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.SbtNativePackager.Linux
import com.typesafe.sbt.digest.Import.digest
import com.typesafe.sbt.gzip.Import.gzip
import com.typesafe.sbt.packager.Keys.maintainer
import com.typesafe.sbt.web.Import.pipelineStages
import play.sbt.PlayImport
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}

object PimpBuild {
  val malliinaGroup = "com.malliina"

  lazy val pimpWeb = PlayProject.server("pimpweb")
    .settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    organization := "org.musicpimp",
    version := "1.9.1",
    scalaVersion := "2.11.8",
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("malliina", "maven")
    ),
    pipelineStages := Seq(digest, gzip),
    libraryDependencies ++= Seq(
      malliinaGroup %% "util-azure" % "2.1.0",
      malliinaGroup %% "util-play" % "3.6.9",
      "org.pegdown" % "pegdown" % "1.6.0",
      "com.amazonaws" % "aws-java-sdk-s3" % "1.11.113",
      PlayImport.filters
    )
  )

  lazy val pimpSettings = commonSettings ++ buildMetaSettings ++ linuxSettings

  def buildMetaSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.malliina.pimpweb"
  )

  def linuxSettings = {
    LinuxPlugin.playSettings ++ Seq(
      httpPort in Linux := Option("8462"),
      httpsPort in Linux := Option("disabled"),
      maintainer := "Michael Skogberg <malliina123@gmail.com>"
    )
  }
}
