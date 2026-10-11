import com.malliina.build.FileIO

import java.nio.file.Path
import sbt.*
import sbtcrossproject.CrossPlugin.autoImport.crossProject
import com.malliina.rollup.EsbuildPlugin.autoImport.npmRoot
import scala.sys.process.Process
import scala.util.Try

val buildDocs = taskKey[Unit]("Builds MkDocs")

val commonSettings = Seq(
  organization := "org.musicpimp",
  scalaVersion := versions.scala
)
val siteTarget = settingKey[Path]("Content target")
ThisBuild / siteTarget := (baseDirectory.value / "site").toPath

val shared = crossProject(JSPlatform, JVMPlatform)
  .settings(commonSettings)

val sharedJs = shared.js
val sharedJvm = shared.jvm

val client: Project = project
  .in(file("client"))
  .enablePlugins(EsbuildPlugin, NodeJsPlugin)
  .dependsOn(sharedJs)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.malliina" %% "util-html" % versions.utilBase
    ),
    publish / skip := true
  )

val generator: Project = project
  .in(file("generator"))
  .enablePlugins(GeneratorPlugin, BuildInfoPlugin, NodeJsPlugin, NetlifyApiPlugin)
  .dependsOn(sharedJvm)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.malliina" %% "util-html" % versions.utilBase,
      "commons-codec" % "commons-codec" % versions.commonsCodec,
      "org.slf4j" % "slf4j-api" % versions.slf4j,
      "ch.qos.logback" % "logback-classic" % versions.logback
    ),
    scalajsProject := client,
    buildDocs := {
      val cmd = "mkdocs build"
      val exitCode = Process(cmd).run(streams.value.log).exitValue()
      if (exitCode != 0)
        sys.error(s"Invalid exit code for '$cmd': $exitCode.")
    },
    buildDocs := Def
      .uncached(Def.task(FileIO.copyDir((ThisBuild / siteTarget).value, assetsRoot.value)))
      .dependsOn(Def.task(IO.delete((ThisBuild / siteTarget).value.toFile / "404.html")))
      .dependsOn(buildDocs)
      .value,
    build := build.dependsOn(buildDocs).value,
    releasePublishArtifactsAction := {},
    publishTo := Option(Resolver.defaultLocal),
    buildInfoKeys ++= Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      "npmRoot" -> (client / npmRoot).value.toFile
    ),
    buildInfoPackage := "com.malliina.generator"
  )

val pimpweb = project
  .in(file("."))
  .aggregate(client, generator)
  .settings(
    build := (generator / build).value,
    deploy := (generator / deploy).value,
    releaseProcess := (generator / releaseProcess).value,
    publishTo := Option(Resolver.defaultLocal),
    publish / skip := true
  )

def gitHash: String =
  Try(Process("git rev-parse --short HEAD").lazyLines.head).toOption.getOrElse("unknown")

Global / onChangedBuildSource := ReloadOnSourceChanges
