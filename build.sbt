import com.malliina.sbt.filetree.DirMap
import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.packager.Keys.maintainer
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}

val pimpWeb = PlayProject.server("pimpweb").enablePlugins(FileTreePlugin)
val malliinaGroup = "com.malliina"
val utilPlayDep = malliinaGroup %% "util-play" % "4.18.1"

organization := "org.musicpimp"
version := "1.11.1"
scalaVersion := "2.12.8"
resolvers += Resolver.bintrayRepo("malliina", "maven")
pipelineStages := Seq(digest, gzip)
libraryDependencies ++= Seq(
  utilPlayDep,
  utilPlayDep % Test classifier "tests",
  malliinaGroup %% "logstreams-client" % "1.3.0",
  malliinaGroup %% "util-base" % "1.7.1",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.475",
  filters,
  "org.seleniumhq.selenium" % "selenium-java" % "3.14.0" % Test
)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)
buildInfoPackage := "com.malliina.pimpweb"

httpPort in Linux := Option("8462")
httpsPort in Linux := Option("disabled")
maintainer := "Michael Skogberg <malliina123@gmail.com>"

fileTreeSources ++= (resourceDirectories in Assets).value.map { dir =>
  val dest =
    if (dir.name == "main") "com.malliina.pimpweb.css.LessAssets"
    else "com.malliina.pimpweb.assets.AppAssets"
  DirMap(dir, dest, "controllers.PimpAssets.at")
}

// WTF?
linuxPackageSymlinks := linuxPackageSymlinks.value.filterNot(_.link == "/usr/bin/starter")
javaOptions in Universal ++= {
  val linuxName = (name in Linux).value
  Seq(
    s"-Dconfig.file=/etc/$linuxName/production.conf",
    s"-Dlogger.file=/etc/$linuxName/logback-prod.xml"
  )
}
