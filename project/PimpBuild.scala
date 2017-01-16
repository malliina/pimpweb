import com.malliina.sbt.unix.LinuxKeys._
import com.malliina.sbt.unix.LinuxPlugin
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.SbtNativePackager.{Linux, Universal}
import com.typesafe.sbt.packager
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.BuildInfoKey

object PimpBuild {
  lazy val pimpWeb = PlayProject.default("pimpweb")
    .enablePlugins(BuildInfoPlugin)
    .settings(pimpSettings: _*)

  lazy val commonSettings = Seq(
    version := "1.7.0",
    scalaVersion := "2.11.8",
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("malliina", "maven")
    ),
    libraryDependencies ++= deps
  )

  val malliinaGroup = "com.malliina"

  val deps = Seq(
    malliinaGroup %% "util-azure" % "2.1.0",
    malliinaGroup %% "play-base" % "3.3.3",
    "org.pegdown" % "pegdown" % "1.6.0",
    "com.lihaoyi" %% "scalatags" % "0.6.2"
  )

  lazy val pimpSettings = commonSettings ++ buildMetaSettings ++ linuxSettings

  def buildMetaSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.malliina.pimpweb"
  )

  def linuxSettings = {
    LinuxPlugin.playSettings ++ Seq(
      httpPort in Linux := Option("disabled"),
      httpsPort in Linux := Option("8462"),
      packager.Keys.maintainer := "Michael Skogberg <malliina123@gmail.com>",
      javaOptions in Universal ++= {
        Seq(
          "-Dfile.encoding=UTF-8",
          "-Dsun.jnu.encoding=UTF-8"
        )
      }
    )
  }
}
