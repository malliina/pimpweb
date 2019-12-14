import java.nio.file.Path

import autowire._
import com.lihaoyi.workbench.Api
import com.lihaoyi.workbench.WorkbenchBasePlugin.server
import sbt._
import sbtcrossproject.CrossPlugin.autoImport.crossProject

import scala.concurrent.ExecutionContext
import scala.sys.process.Process
import scala.util.Try

val utilHtmlVersion = "5.2.4"
val scalatagsVersion = "0.7.0"
val buildDocs = taskKey[Unit]("Builds MkDocs")
val deployDocs = taskKey[Unit]("Deploys docs.musicpimp.org")
val deployNetlify = taskKey[Unit]("Runs 'netlify deploy --prod'")
val buildSite = taskKey[Unit]("Builds local files")
val buildAndDeploy = taskKey[Unit]("Builds and deploys")

val commonSettings = Seq(
  organization := "org.musicpimp",
  scalaVersion := "2.13.1"
)
val siteTarget = settingKey[Path]("Content target")
ThisBuild / siteTarget := (target.value / "dist").toPath

val shared = crossProject(JSPlatform, JVMPlatform)
  .settings(commonSettings)

val sharedJs = shared.js
val sharedJvm = shared.jvm

val client: Project = project
  .in(file("client"))
  .enablePlugins(GeneratorClientPlugin, NodeJsPlugin)
  .dependsOn(sharedJs)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.malliina" %%% "util-html" % utilHtmlVersion
    ),
    version in webpack := "4.39.1",
    version in startWebpackDevServer := "3.7.2",
    //    webpackBundlingMode := BundlingMode.LibraryOnly(),
    emitSourceMaps := false,
    npmDependencies in Compile ++= Seq(
      "@fortawesome/fontawesome-free" -> "5.10.1",
      "bootstrap" -> "4.3.1",
      "jquery" -> "3.4.1",
      "popper.js" -> "1.15.0"
    ),
    npmDevDependencies in Compile ++= Seq(
      "autoprefixer" -> "9.6.1",
      "cssnano" -> "4.1.10",
      "css-loader" -> "3.2.0",
      "file-loader" -> "4.2.0",
      "less" -> "3.9.0",
      "less-loader" -> "5.0.0",
      "mini-css-extract-plugin" -> "0.8.0",
      "postcss-import" -> "12.0.1",
      "postcss-loader" -> "3.0.0",
      "postcss-preset-env" -> "6.7.0",
      "style-loader" -> "1.0.0",
      "url-loader" -> "2.1.0",
      "webpack-merge" -> "4.2.1"
    ),
    workbenchDefaultRootObject := {
      val dist = siteTarget.value
      Some((s"$dist/index.html", s"$dist/"))
    },
    skip in publish := true
  )

val generator: Project = project
  .in(file("generator"))
  .enablePlugins(ContentPlugin, BuildInfoPlugin, NodeJsPlugin)
  .dependsOn(sharedJvm)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.malliina" %% "util-html" % utilHtmlVersion
    ),
    jsProject := client,
    refreshBrowsers := {
      implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
      (server in client).value.Wire[Api].reload().call()
    },
    refreshBrowsers := refreshBrowsers.triggeredBy(build).value,
    bucket := "www.musicpimp.org",
    distDirectory := siteTarget.value,
    buildDocs := {
      val cmd = "mkdocs build"
      val exitCode = Process(cmd).run(streams.value.log).exitValue()
      if (exitCode != 0)
        sys.error(s"Invalid exit code for '$cmd': $exitCode.")
    },
    deployDocs := {
      val exitCode = Process("mkdocs gh-deploy").run(streams.value.log).exitValue()
      if (exitCode != 0)
        sys.error(s"Invalid exit code for 'mkdocs gh-deploy': $exitCode.")
    },
    deployNetlify := {
      val cmd = "netlify deploy --prod"
      val exitCode = Process(cmd).run(streams.value.log).exitValue()
      if (exitCode != 0)
        sys.error(s"Invalid exit code for '$cmd': $exitCode.")
    },
    buildSite := Def.sequential(buildDocs, build).value,
    buildAndDeploy := Def.sequential(buildDocs, build, deployNetlify).value,
//    releasePublishArtifactsAction := Def.sequential(buildDocs, build, deployNetlify).value,
    //    releasePublishArtifactsAction := Def.sequential(publish, build, deployDocs).value,
    releasePublishArtifactsAction := {},
    publishTo := Option(Resolver.defaultLocal),
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, "gitHash" -> gitHash),
    buildInfoPackage := "com.malliina.generator"
  )

val pimpweb = project
  .in(file("."))
  .aggregate(client, generator)
  .settings(
    build := build.in(generator).value,
    releaseProcess := releaseProcess.in(generator).value,
    publishTo := Option(Resolver.defaultLocal),
    skip in publish := true
  )

def gitHash: String =
  Try(Process("git rev-parse --short HEAD").lineStream.head).toOption.getOrElse("unknown")
