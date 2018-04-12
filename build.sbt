import com.malliina.sbt.filetree.DirMap
import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.packager.Keys.maintainer
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}

val pimpWeb = PlayProject.server("pimpweb").enablePlugins(FileTreePlugin)
val malliinaGroup = "com.malliina"
val utilPlayDep = malliinaGroup %% "util-play" % "4.11.0"

organization := "org.musicpimp"
version := "1.11.0"
scalaVersion := "2.12.5"
resolvers += Resolver.bintrayRepo("malliina", "maven")
pipelineStages := Seq(digest, gzip)
libraryDependencies ++= Seq(
  utilPlayDep,
  utilPlayDep % Test classifier "tests",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.313",
  "com.vladsch.flexmark" % "flexmark-html-parser" % "0.32.20",
  filters,
  "org.seleniumhq.selenium" % "selenium-java" % "3.11.0" % Test
)

dependencyOverrides ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.8",
  "com.typesafe.akka" %% "akka-stream" % "2.5.8"
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
