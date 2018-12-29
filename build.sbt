import com.malliina.sbt.filetree.DirMap
import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.packager.Keys.maintainer
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}
import sbt._

val dummy = Home.androidAppUri
val dummy2 = PimpWebHtml.subHeader
val malliinaGroup = "com.malliina"
val utilPlayDep = malliinaGroup %% "util-play" % "4.18.1"

ThisBuild / organization := "org.musicpimp"
ThisBuild / version := "1.11.1"
ThisBuild / scalaVersion := "2.12.8"

val Static = config("static")
val buildSite = taskKey[BuiltSite]("Build the site")
val buildSiteDev = taskKey[BuiltSite]("Build the site in dev mode")

//TaskKey[Unit]("demo") := {
//  Site.build(SiteSpec(Nil, Nil, (target in Static).value.toPath))
//}
//
//TaskKey[Unit]("site") := {
//  Site.build(SiteSpec(Nil, Nil, (target in Static).value.toPath))
//}

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
    Static / target := baseDirectory.value / "dist",
    buildSite := {
      val siteDir = (target in Static).value.toPath
      val files = (webpack in(Compile, fullOptJS)).value
      val mappings =
        files.map { file => FileMapping(file.data.toPath, file.data.name) } ++
          (baseDirectory.value / "img").listFiles().map { img => FileMapping(img.toPath, img.name)}
      // Excludes scripts emitted from CSS extraction
      val excludedScripts = Seq("styles", "fonts")
      val assets = AssetHelper.assetGroup(files, excludedScripts)
      streams.value.log.info("Building...")
      Site.build(SiteSpec(assets.styles, assets.scripts, mappings, siteDir), streams.value.log)
    },
    (webpack in(Compile, fastOptJS)) := {
      val siteDir = (crossTarget in(Compile, fastOptJS)).value.toPath
      val files = (webpack in(Compile, fastOptJS)).value
      val mappings =
        files.map { file => FileMapping(file.data.toPath, file.data.name) } ++
          (baseDirectory.value / "img").listFiles().map { img => FileMapping(img.toPath, img.name)}
      // Excludes scripts emitted from CSS extraction
      val excludedScripts = Nil
      val assets = AssetHelper.assetGroup(files, excludedScripts)
      val log = streams.value.log
      log.info("Building...")
      Site.build(SiteSpec(assets.styles, assets.scripts, mappings, siteDir), log)
      files
    }
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
