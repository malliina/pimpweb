import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbt.unix.LinuxPlugin
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.SbtNativePackager.{Debian, Linux, Universal}
import com.typesafe.sbt.digest.Import.digest
import com.typesafe.sbt.gzip.Import.gzip
import com.typesafe.sbt.packager
import com.typesafe.sbt.packager.Keys.serverLoading
import com.typesafe.sbt.packager.archetypes.{JavaServerAppPackaging, ServerLoader}
import com.typesafe.sbt.web.Import.pipelineStages
import play.sbt.PlayImport
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}
import sbtbuildinfo.{BuildInfoKey, BuildInfoPlugin}

object PimpBuild {
  val malliinaGroup = "com.malliina"

  lazy val pimpWeb = PlayProject.default("pimpweb")
    .enablePlugins(JavaServerAppPackaging, BuildInfoPlugin)
    .settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    organization := "org.musicpimp",
    version := "1.8.8",
    scalaVersion := "2.11.8",
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("malliina", "maven")
    ),
    pipelineStages := Seq(digest, gzip),
    libraryDependencies ++= Seq(
      malliinaGroup %% "util-azure" % "2.1.0",
      malliinaGroup %% "util-play" % "3.6.7",
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
      packager.Keys.maintainer := "Michael Skogberg <malliina123@gmail.com>",
      javaOptions in Universal ++= Seq(
        "-Dfile.encoding=UTF-8",
        "-Dsun.jnu.encoding=UTF-8"
      ),
      serverLoading in Debian := ServerLoader.Systemd
    )
  }
}
