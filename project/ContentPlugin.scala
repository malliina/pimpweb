import java.nio.file.Path

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{fastOptJS, fullOptJS}
import sbt.Keys._
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.{BundlerFileType, BundlerFileTypeAttr, webpack}

/** Controls builds and deployments of a static website.
  *
  * Task `build` prepares the site locally. Task `deploy` deploys the site.
  *
  * Specify the JS project, used to build assets, in key `jsProject`.
  */
object ContentPlugin extends AutoPlugin {
  object autoImport {
    val deployTarget = settingKey[DeployTarget]("Target deployment platform")
    val bucket = settingKey[String]("Target bucket name of website in Google Cloud Storage")
    val distDirectory = settingKey[Path]("Static site target directory")
    val prepareBuildspec = taskKey[Path]("Creates the buildspec")
    val build = taskKey[Unit]("Builds the website")
    val deploy = taskKey[Unit]("Deploys the website")
    val produceManifestDev = taskKey[Path]("Builds the site assets file")
    val produceManifestProd = taskKey[Path]("Builds the site assets file")
    val Static = config("static")
    val jsProject = settingKey[Project]("Scala.js project")
    val fastWebpack = taskKey[Seq[Attributed[File]]]("Dev webpack")
    val fullWebpack = taskKey[Seq[Attributed[File]]]("Prod webpack")
    val dir = settingKey[Path]("Directory")
    val assetTarget = settingKey[File]("Assets target (from webpack)")
    val manifestFile = settingKey[Path]("Path to manifest file")
    val buildspec = settingKey[Path]("Path to buildspec file")
  }

  import autoImport._

  val clientProject = Def.settingDyn(jsProject)

  override def projectSettings: Seq[Setting[_]] = Seq(
    exportJars := false,
    deployTarget := DeployTarget.NetlifyTarget,
    assetTarget := Def.settingDyn(crossTarget.in(clientProject.value, Compile, fullOptJS in clientProject.value)).value,
    distDirectory := (target.value / "dist").toPath,
    manifestFile := (target.value / "site.json").toPath,
    buildspec := (target.value / "buildspec.json").toPath,
    // Triggers compilation on code changes in either project
    watchSources := watchSources.value ++ Def.taskDyn(watchSources in clientProject.value).value,
    fastWebpack := Def.taskDyn {
      webpack.in(clientProject.value, Compile, fastOptJS in clientProject.value)
    }.value,
    fullWebpack := Def.taskDyn {
      webpack.in(clientProject.value, Compile, fullOptJS in clientProject.value)
    }.value,
    publishLocal in Static := build.value,
    clean in Static := FileIO.deleteDirectory(distDirectory.value),
    // https://github.com/sbt/sbt/issues/2975#issuecomment-358709526
    build := Def.taskDyn {
      val spec = prepareBuildspec.value
      run in Compile toTask s" $spec"
    }.value,
    deploy := Def.taskDyn {
      val spec = BuildSpec(Command.Deploy, produceManifestProd.value, deployTarget.value)
      val path = FileIO.writeJson(spec, buildspec.value, streams.value.log)
      run in Compile toTask s" $path"
    }.value,
    prepareBuildspec := {
      val spec = BuildSpec(Command.Build, produceManifestProd.value, deployTarget.value)
      FileIO.writeJson(spec, buildspec.value, streams.value.log)
    },
    produceManifestDev := assetGroup(fastWebpack.value, adhocScripts = Seq("/workbench.js"))
      .manifest(assetTarget.value)
      .to(manifestFile.value, streams.value.log),
    produceManifestProd := assetGroup(fullWebpack.value)
      .manifest(assetTarget.value)
      .to(manifestFile.value, streams.value.log),
    produceManifestProd := produceManifestProd.dependsOn(clean in Static).value,
    publish in Static := deploy.value,
    publish := deploy.value,
    // Hack to make the default release process work instead of fake error "Repository for publishing is not specified"
    publishTo := Option(Resolver.defaultLocal),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.7.0",
      "org.slf4j" % "slf4j-api" % "1.7.27",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.qos.logback" % "logback-core" % "1.2.3",
      "com.google.cloud" % "google-cloud-storage" % "1.86.0"
    )
  )

  def assetGroup(
    files: Seq[Attributed[File]],
    excludePrefixes: Seq[String] = Seq("styles", "fonts", "vendors"),
    adhocScripts: Seq[String] = Nil
  ): AssetGroup = {
    def filesOf(fileType: BundlerFileType) = files.filter(_.metadata.get(BundlerFileTypeAttr).contains(fileType))

    // Orders library scripts before app scripts
    val apps = filesOf(BundlerFileType.Application) ++ filesOf(BundlerFileType.ApplicationBundle)
    val libraries = filesOf(BundlerFileType.Library)
    val assets = filesOf(BundlerFileType.Asset)
    val loaders = filesOf(BundlerFileType.Loader)
    val scripts = (apps ++ loaders ++ assets ++ libraries).distinct.reverse
      .map(_.data)
      .filter(f => f.ext == "js" && !excludePrefixes.exists(e => f.name.startsWith(e)))
      .distinct
    val styles = files.map(_.data).filter(_.ext == "css")
    val statics = files.map(_.data).filter(f => f.ext != "css" && f.ext != "js")
    AssetGroup(scripts, adhocScripts, styles, statics)
  }
}
