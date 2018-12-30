import com.malliina.sbt.filetree.DirMap
import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.packager.Keys.maintainer
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}
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

val client = project.in(file("client"))
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(
    version in webpack := "4.28.2",
    version in startWebpackDevServer := "3.1.4",
    emitSourceMaps := false,
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
    (webpack in(Compile, fastOptJS)) := {
      val log = streams.value.log
      log.info("Running webpack...")
      val files = (webpack in(Compile, fastOptJS)).value
      files
    }
  )

val runSite = taskKey[Unit]("Runs the generator")
val prepareDeploy = taskKey[Unit]("Build a prod version")
val deploy = taskKey[Unit]("Deploys the website")

val generator = project.in(file("generator"))
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
    prepareDeploy := Def.taskDyn {
      val files = webpack.in(client, Compile, fullOptJS in client).value
      val assets = AssetHelper.assetGroup(files, Seq("styles", "fonts"))
      val css = assets.styles.mkString(" ")
      val js = assets.scripts.mkString(" ")
      run in Compile toTask s" prepare ${distDir.value} $css $js"
    }.value,
    deploy := Def.taskDyn { run in Compile toTask s" deploy ${distDir.value}" }.value,
    deploy := deploy.dependsOn(prepareDeploy).value
  )

val pimpWeb = PlayProject.server("pimpweb")
  .enablePlugins(FileTreePlugin, WebScalaJSBundlerPlugin)
  .settings(
    resolvers += Resolver.bintrayRepo("malliina", "maven"),
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    libraryDependencies ++= Seq(
      utilPlayDep,
      utilPlayDep % Test classifier "tests",
      malliinaGroup %% "logstreams-client" % "1.3.0",
      malliinaGroup %% "util-base" % "1.7.1",
      "com.amazonaws" % "aws-java-sdk-s3" % "1.11.475",
      filters,
      "org.seleniumhq.selenium" % "selenium-java" % "3.14.0" % Test
    ),
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.malliina.pimpweb",
    httpPort in Linux := Option("8462"),
    httpsPort in Linux := Option("disabled"),
    maintainer := "Michael Skogberg <malliina123@gmail.com>",
    fileTreeSources ++= (resourceDirectories in Assets).value.map { dir =>
      val dest =
        if (dir.name == "main") "com.malliina.pimpweb.css.LessAssets"
        else "com.malliina.pimpweb.assets.AppAssets"
      DirMap(dir, dest, "controllers.PimpAssets.at")
    },
    // WTF?
    linuxPackageSymlinks := linuxPackageSymlinks.value.filterNot(_.link == "/usr/bin/starter"),
    javaOptions in Universal ++= {
      val linuxName = (name in Linux).value
      Seq(
        s"-Dconfig.file=/etc/$linuxName/production.conf",
        s"-Dlogger.file=/etc/$linuxName/logback-prod.xml"
      )
    }
  )
