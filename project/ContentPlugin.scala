import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{fastOptJS, fullOptJS}
import sbt.Keys._
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.{BundlerFileType, BundlerFileTypeAttr, webpack}

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
    val prepare = taskKey[Unit]("Builds the site for deployment")
    val deploy = taskKey[Unit]("Deploys the website")
    val website = taskKey[Unit]("JSONs the website")
    val buildManifest = taskKey[Path]("Builds the site manifest")
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
    // https://github.com/sbt/sbt/issues/2975#issuecomment-358709526
    build := Def.taskDyn {
      val assets = prepareRelative(fastWebpack.value, distDirectory.value, assetTarget.value)
      run in Compile toTask s" build ${distDirectory.value} /workbench.js $assets"
    }.value,
    publishLocal in Static := build.value,
    clean in Static := deleteDirectory(distDirectory.value),
    prepare := Def
      .taskDyn {
        val assets = prepareRelative(fullWebpack.value, distDirectory.value, assetTarget.value)
        run in Compile toTask s" prepare ${distDirectory.value} $assets"
      }
      .dependsOn(clean in Static)
      .value,
    deploy := Def
      .taskDyn {
        run in Compile toTask s" deploy ${distDirectory.value} ${bucket.value}"
      }
      .dependsOn(prepare)
      .value,
    website := Def
      .taskDyn {
        run in Compile toTask s" website ${distDirectory.value} ${bucket.value}"
      }
      .dependsOn(prepare)
      .value,
    buildManifest := Def.taskDyn {
      val siteFile = manifestFile.value
      val siteManifest = prepareManifest(fastWebpack.value, distDirectory.value, assetTarget.value).to(siteFile)
      (run in Compile toTask s" manifest ${siteFile.toAbsolutePath.toString}").map(_ => siteFile)
    }.dependsOn(clean in Static).value,
    publish in Static := deploy.value,
    publish := deploy.value,
    // Hack to make the default release process work instead of fake error "Repository for publishing is not specified"
    publishTo := Option(Resolver.defaultLocal)
  )

  case class AssetGroup(scripts: Seq[File], styles: Seq[File], statics: Seq[File])

  def prepareRelative(files: Seq[Attributed[File]],
                      distBase: Path,
                      crossBase: File,
                      excludePrefixes: Seq[String] = Seq("styles", "fonts", "vendors")) = {
    val eligible = assetGroup(files, excludePrefixes)
    val assetsDir = distBase.toFile / "assets"

    def copyAndRelativize(subDir: String, file: File) = {
      val dest = assetsDir / subDir / file.name
      if (file.getAbsolutePath != dest.getAbsolutePath)
        IO.copyFile(file, dest)
      distBase.relativize(dest.toPath)
    }

    val statics = eligible.statics.flatMap { s =>
      crossBase.relativize(s).map { relative =>
        val dest = distBase.resolve(relative.toPath).toFile
        IO.copyFile(s, dest)
        relative.toPath
      }
    }

    val relative =
      eligible.styles.map(copyAndRelativize("css", _)) ++
        eligible.scripts.map(copyAndRelativize("js", _)) ++ statics
    relative
      .map { p =>
        val r = p.toFile.getPath
        if (r.startsWith("/")) r else s"/$r"
      }
      .mkString(" ")
  }

  def prepareManifest(files: Seq[Attributed[File]],
                      distBase: Path,
                      crossBase: File,
                      excludePrefixes: Seq[String] = Seq("styles", "fonts", "vendors")): SiteManifest = {
    val eligible = assetGroup(files, excludePrefixes)
    val assetsDir = distBase.toFile / "assets"

    def copyAndRelativize(subDir: String, file: File): Path = {
      val dest = assetsDir / subDir / file.name
      if (file.getAbsolutePath != dest.getAbsolutePath)
        IO.copyFile(file, dest)
      distBase.relativize(dest.toPath)
    }

    val statics = eligible.statics.flatMap { s =>
      crossBase.relativize(s).map { relative =>
        val dest = distBase.resolve(relative.toPath).toFile
        IO.copyFile(s, dest)
        relative.toPath
      }
    }
    SiteManifest(
      eligible.styles.map(copyAndRelativize("css", _)).map(_.toAbsolutePath.toString),
      eligible.scripts.map(copyAndRelativize("js", _)).map(_.toAbsolutePath.toString),
      Nil,
      statics.map(_.toAbsolutePath.toString),
      distBase
    )
  }

  def assetGroup(files: Seq[Attributed[File]], excludePrefixes: Seq[String]): AssetGroup = {
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
    AssetGroup(scripts, styles, statics)
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
