import sbt._

val malliinaGroup = "com.malliina"
val utilPlayDep = malliinaGroup %% "util-play" % "4.18.1"
val Static = config("static")

ThisBuild / organization := "org.musicpimp"
ThisBuild / version := "1.11.1"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / Static / target := baseDirectory.value / "dist"

val distDir = settingKey[File]("Static site target directory")
ThisBuild / distDir := baseDirectory.value / "dist"

lazy val client: Project = project.in(file("client"))
  .enablePlugins(ScalaJSBundlerPlugin)
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
    webpackMonitoredDirectories += distDir.value,
    includeFilter in webpackMonitoredFiles := "*.*",
    webpackDevServerExtraArgs ++= Seq("--content-base", distDir.value.getAbsolutePath),
    (webpack in(Compile, fastOptJS)) := Def.taskDyn {
      val log = streams.value.log
      log.info("Running webpack...")
      val files = (webpack in(Compile, fastOptJS)).value
      val assets = AssetHelper.assetGroup(files, Seq("styles", "fonts"))
      val css = assets.styles.mkString(" ")
      val js = assets.scripts.mkString(" ")
      run.in(generator, Compile).toTask(s" build ${distDir.value} $css $js").map(_ => files)
    }.value
  )

val runSite = taskKey[Unit]("Runs the generator")
val deploy = taskKey[Unit]("Deploys the website")

lazy val generator: Project = project.in(file("generator"))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.6.7",
      "com.malliina" %% "util-html" % "4.18.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.qos.logback" % "logback-core" % "1.2.3",
      "com.google.cloud" % "google-cloud-storage" % "1.55.0"
    ),
    // https://github.com/sbt/sbt/issues/2975#issuecomment-358709526
    runSite := Def.taskDyn {
      val files = webpack.in(client, Compile, fastOptJS in client).value
      val assets = AssetHelper.assetGroup(files, Seq("styles", "fonts"))
      val css = assets.styles.mkString(" ")
      val js = assets.scripts.mkString(" ")
      run in Compile toTask s" build ${distDir.value} $css $js"
    }.value,
    clean in Static := {
      AssetHelper.deleteDirectory(distDir.value.toPath)
    },
    stage in Static := Def.taskDyn {
      val files = webpack.in(client, Compile, fullOptJS in client).value
      val assets = AssetHelper.assetGroup(files, Seq("styles", "fonts"))
      val css = assets.styles.mkString(" ")
      val js = assets.scripts.mkString(" ")
      (run in Compile toTask s" prepare ${distDir.value} $css $js").map(_ => distDir.value)
    }.dependsOn(clean in Static).value,
    deploy := Def.taskDyn { run in Compile toTask s" deploy ${distDir.value}" }.value,
    deploy := deploy.dependsOn(stage in Static).value
  )
