import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.packager.Keys.maintainer
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}

val pimpWeb = PlayProject.server("pimpweb")
val malliinaGroup = "com.malliina"

organization := "org.musicpimp"
version := "1.9.1"
scalaVersion := "2.11.8"
resolvers ++= Seq(
  Resolver.bintrayRepo("malliina", "maven")
)
pipelineStages := Seq(digest, gzip)
libraryDependencies ++= Seq(
  malliinaGroup %% "util-azure" % "2.1.0",
  malliinaGroup %% "util-play" % "3.6.9",
  "org.pegdown" % "pegdown" % "1.6.0",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.113",
  filters
)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)
buildInfoPackage := "com.malliina.pimpweb"

httpPort in Linux := Option("8462")
httpsPort in Linux := Option("disabled")
maintainer := "Michael Skogberg <malliina123@gmail.com>"
