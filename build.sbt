import java.nio.file.Path

import com.lihaoyi.workbench.Api
import com.lihaoyi.workbench.WorkbenchBasePlugin.server
import sbt._
import autowire._
import sbtcrossproject.CrossPlugin.autoImport.crossProject

import scala.concurrent.ExecutionContext

val malliinaGroup = "com.malliina"
val utilPlayDep = malliinaGroup %% "util-play" % "4.18.1"
val Static = config("static")

ThisBuild / Static / target := target.value / "dist"

val distDirectory = settingKey[Path]("Static site target directory")
ThisBuild / distDirectory := (target.value / "dist").toPath

val commonSettings = Seq(
  organization := "org.musicpimp",
  version := "0.0.1",
  scalaVersion := "2.12.8"
)

val shared = crossProject(JSPlatform, JVMPlatform)
  .settings(commonSettings)

val sharedJs = shared.js
val sharedJvm = shared.jvm

val client: Project = project.in(file("client"))
  .enablePlugins(ScalaJSBundlerPlugin, WorkbenchBasePlugin)
  .dependsOn(sharedJs)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.2",
      "com.lihaoyi" %%% "scalatags" % "0.6.7",
      "com.malliina" %%% "util-html" % "4.18.1",
      "com.typesafe.play" %%% "play-json" % "2.6.11"
    ),
    version in webpack := "4.28.2",
    version in startWebpackDevServer := "3.1.4",
//    webpackBundlingMode := BundlingMode.LibraryOnly(),
    emitSourceMaps := false,
    npmDependencies in Compile ++= Seq(
      "jquery" -> "3.3.1",
      "popper.js" -> "1.14.6",
      "bootstrap" -> "4.2.1"
    ),
    npmDevDependencies in Compile ++= Seq(
      "webpack-merge" -> "4.1.5",
      "style-loader" -> "0.23.1",
      "css-loader" -> "2.1.0",
      "less" -> "3.9.0",
      "less-loader" -> "4.1.0",
      "url-loader" -> "1.1.2",
      "mini-css-extract-plugin" -> "0.5.0",
      "postcss-loader" -> "3.0.0",
      "postcss-import" -> "12.0.1",
      "postcss-preset-env" -> "6.5.0",
      "autoprefixer" -> "9.4.3",
      "cssnano" -> "4.1.8"
    ),
    scalaJSUseMainModuleInitializer := true,
    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack.dev.config.js"),
    webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack.prod.config.js"),
    workbenchDefaultRootObject := Some((s"${distDirectory.value}/index.html", s"${distDirectory.value}/"))
  )

val bucket = settingKey[String]("Bucket name")
val build = taskKey[Unit]("Builds the website")
val prepare = taskKey[Unit]("Builds the site for deployment")
val deploy = taskKey[Unit]("Deploys the website")

val generator: Project = project.in(file("generator"))
  .dependsOn(sharedJvm)
  .settings(commonSettings)
  .settings(
    exportJars := false,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.6.7",
      "com.malliina" %% "util-html" % "4.18.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.qos.logback" % "logback-core" % "1.2.3",
      "com.google.cloud" % "google-cloud-storage" % "1.55.0"
    ),
    watchSources := watchSources.value ++ (watchSources in client).value,
    refreshBrowsers := {
      implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
      (server in client).value.Wire[Api].reload().call()
    },
    refreshBrowsers := refreshBrowsers.triggeredBy(build).value,
    bucket := "www.musicpimp.org",
    // https://github.com/sbt/sbt/issues/2975#issuecomment-358709526
    build := Def.taskDyn {
      val files = webpack.in(client, Compile, fastOptJS in client).value
      val assets = AssetHelper.prepareRelative(files, Seq("styles", "fonts"), distDirectory.value)
      run in Compile toTask s" build ${distDirectory.value} $assets"
    }.value,
    clean in Static := AssetHelper.deleteDirectory(distDirectory.value),
    prepare := Def.taskDyn {
      val files = webpack.in(client, Compile, fullOptJS in client).value
      val assets = AssetHelper.prepareRelative(files, Seq("styles", "fonts"), distDirectory.value)
      run in Compile toTask s" prepare ${distDirectory.value} $assets"
    }.dependsOn(clean in Static).value,
    deploy := Def.taskDyn { run in Compile toTask s" deploy ${distDirectory.value} ${bucket.value}" }
      .dependsOn(prepare).value,
  )
