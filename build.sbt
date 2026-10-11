import com.malliina.build.FileIO

import java.nio.file.Path
import sbt.*
import sbtcrossproject.CrossPlugin.autoImport.crossProject

import scala.sys.process.Process
import scala.util.Try

val utilHtmlVersion = "6.17.0"
val scalatagsVersion = "0.13.1"
val buildDocs = taskKey[Unit]("Builds MkDocs")

val commonSettings = Seq(
  organization := "org.musicpimp",
  scalaVersion := "3.10.0"
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
      "com.malliina" %% "util-html" % utilHtmlVersion
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
      "com.malliina" %% "util-html" % utilHtmlVersion,
      "commons-codec" % "commons-codec" % "1.22.1",
      "org.slf4j" % "slf4j-api" % "2.0.20"
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
    deploy := deploy.dependsOn(buildDocs).value,
    releasePublishArtifactsAction := {},
    publishTo := Option(Resolver.defaultLocal),
    buildInfoKeys ++= Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      "npmRoot" -> (client / com.malliina.rollup.EsbuildPlugin.autoImport.npmRoot).value.toFile
    ),
    buildInfoPackage := "com.malliina.generator"
  )

val pimpweb = project
  .in(file("."))
  .aggregate(client, generator)
  .settings(
    build := (generator / build).value,
    releaseProcess := (generator / releaseProcess).value,
    publishTo := Option(Resolver.defaultLocal),
    publish / skip := true
  )

def gitHash: String =
  Try(Process("git rev-parse --short HEAD").lazyLines.head).toOption.getOrElse("unknown")

Global / onChangedBuildSource := ReloadOnSourceChanges
