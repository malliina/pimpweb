import com.malliina.sbt.filetree.DirMap
import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.packager.Keys.maintainer
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}

val pimpWeb = PlayProject.server("pimpweb").enablePlugins(FileTreePlugin)
val malliinaGroup = "com.malliina"

organization := "org.musicpimp"
version := "1.10.2"
scalaVersion := "2.12.4"
resolvers ++= Seq(
  Resolver.bintrayRepo("malliina", "maven")
)
pipelineStages := Seq(digest, gzip)
libraryDependencies ++= Seq(
  malliinaGroup %% "util-play" % "4.3.10",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.221",
  "com.vladsch.flexmark" % "flexmark-html-parser" % "0.27.0",
  filters
)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)
buildInfoPackage := "com.malliina.pimpweb"

httpPort in Linux := Option("8462")
httpsPort in Linux := Option("disabled")
maintainer := "Michael Skogberg <malliina123@gmail.com>"

fileTreeSources ++= (resourceDirectories in Assets).value.map { dir =>
  if(dir.name == "main") DirMap(dir, "com.malliina.pimpweb.css.LessAssets", "controllers.PimpAssets.at")
  else DirMap(dir, "com.malliina.pimpweb.assets.AppAssets", "controllers.PimpAssets.at")
}
