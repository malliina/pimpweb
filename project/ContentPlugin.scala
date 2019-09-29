import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor, StandardOpenOption}

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{fastOptJS, fullOptJS}
import play.api.libs.json.Json
import sbt.Keys._
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.{BundlerFileType, BundlerFileTypeAttr, webpack}

case class AssetGroup(scripts: Seq[File], adhocScripts: Seq[String], styles: Seq[File], statics: Seq[File]) {
  def manifest(assetsBase: File): AssetsManifest =
    AssetsManifest(scripts.map(_.toPath), adhocScripts, styles.map(_.toPath), statics.map(_.toPath), assetsBase.toPath)
}

/**
  *
  * @param assetsBase typically .../scalajs-bundler/main
  */
case class AssetsManifest(scripts: Seq[Path],
                          adhocScripts: Seq[String],
                          styles: Seq[Path],
                          statics: Seq[Path],
                          assetsBase: Path) {
  def to(file: Path) = {
    val pretty = Json.prettyPrint(AssetsManifest.json.writes(this))
    Files.write(file, pretty.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE).toAbsolutePath
  }
}

object AssetsManifest {
  implicit val paths = Formats.pathFormat
  implicit val json = Json.format[AssetsManifest]
}

/** Controls builds and deployments of a static website.
  *
  * The `build` task uses webpack to build assets, then provides built assets as program
  * arguments to the run task. The run task will run the main method of the SBT project
  * this plugin is enabled for. You may trigger HTML generation from the main method
  * with the given arguments.
  *
  * Similarly, use `prepare` to prep prod assets, and `deploy` to deploy the website to
  * `bucket`.
  *
  * Specify the JS project, used to build assets, in key `jsProject`.
  */
object ContentPlugin extends AutoPlugin {

  object autoImport {
    val bucket = settingKey[String]("Target bucket name of website in Google Cloud Storage")
    val distDirectory = settingKey[Path]("Static site target directory")
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
  }

  import autoImport._

  val clientProject = Def.settingDyn(jsProject)

  override def projectSettings: Seq[Setting[_]] = Seq(
    exportJars := false,
    assetTarget := Def.settingDyn(crossTarget.in(clientProject.value, Compile, fullOptJS in clientProject.value)).value,
    distDirectory := (target.value / "dist").toPath,
    manifestFile := (target.value / "site.json").toPath,
    // Triggers compilation on code changes in either project
    watchSources := watchSources.value ++ Def.taskDyn(watchSources in clientProject.value).value,
    fastWebpack := Def.taskDyn {
      webpack.in(clientProject.value, Compile, fastOptJS in clientProject.value)
    }.value,
    fullWebpack := Def.taskDyn {
      webpack.in(clientProject.value, Compile, fullOptJS in clientProject.value)
    }.value,
    publishLocal in Static := build.value,
    clean in Static := deleteDirectory(distDirectory.value),
    // https://github.com/sbt/sbt/issues/2975#issuecomment-358709526
    build := Def.taskDyn {
      run in Compile toTask s" build ${produceManifestDev.value}"
    }.value,
    deploy := Def.taskDyn {
      run in Compile toTask s" deploy ${produceManifestDev.value} ${bucket.value}"
    }.value,
    produceManifestDev := assetGroup(fastWebpack.value, adhocScripts = Seq("/workbench.js"))
      .manifest(assetTarget.value)
      .to(manifestFile.value),
    produceManifestProd := assetGroup(fullWebpack.value)
      .manifest(assetTarget.value)
      .to(manifestFile.value),
    produceManifestProd := produceManifestProd.dependsOn(clean in Static).value,
    publish in Static := deploy.value,
    publish := deploy.value,
    // Hack to make the default release process work instead of fake error "Repository for publishing is not specified"
    publishTo := Option(Resolver.defaultLocal)
  )

  def assetGroup(files: Seq[Attributed[File]],
                 excludePrefixes: Seq[String] = Seq("styles", "fonts", "vendors"),
                 adhocScripts: Seq[String] = Nil): AssetGroup = {
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
    AssetGroup(scripts, Seq("/workbench.js"), styles, statics)
  }

  // https://stackoverflow.com/a/27917071
  def deleteDirectory(dir: Path): Path = {
    if (Files.exists(dir)) {
      Files.walkFileTree(
        dir,
        new SimpleFileVisitor[Path] {
          override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
            Files.delete(file)
            FileVisitResult.CONTINUE
          }

          override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
            Files.delete(dir)
            FileVisitResult.CONTINUE
          }
        }
      )
    } else {
      dir
    }
  }
}
