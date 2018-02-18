import com.malliina.sbt.filetree.DirMap
import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.packager.Keys.maintainer
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}

val pimpWeb = PlayProject.server("pimpweb").enablePlugins(FileTreePlugin)
val malliinaGroup = "com.malliina"
val utilPlayDep = malliinaGroup %% "util-play" % "4.6.0"

organization := "org.musicpimp"
version := "1.10.3"
scalaVersion := "2.12.4"
resolvers += Resolver.bintrayRepo("malliina", "maven")
pipelineStages := Seq(digest, gzip)
libraryDependencies ++= Seq(
  utilPlayDep,
  utilPlayDep % Test classifier "tests",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.275",
  "com.vladsch.flexmark" % "flexmark-html-parser" % "0.30.0",
  filters,
  "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % Test
)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)
buildInfoPackage := "com.malliina.pimpweb"

httpPort in Linux := Option("8462")
httpsPort in Linux := Option("disabled")
maintainer := "Michael Skogberg <malliina123@gmail.com>"

fileTreeSources ++= (resourceDirectories in Assets).value.map { dir =>
  if (dir.name == "main") DirMap(dir, "com.malliina.pimpweb.css.LessAssets", "controllers.PimpAssets.at")
  else DirMap(dir, "com.malliina.pimpweb.assets.AppAssets", "controllers.PimpAssets.at")
}

// WTF?
linuxPackageSymlinks := linuxPackageSymlinks.value.filterNot(_.link == "/usr/bin/starter")