import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{fastOptJS, fullOptJS}
import sbt.Keys._
import sbt.{AutoPlugin, _}
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
    val Static = config("static")
    val jsProject = settingKey[Project]("Scala.js project")
    val fastWebpack = taskKey[Seq[Attributed[File]]]("Dev webpack")
    val fullWebpack = taskKey[Seq[Attributed[File]]]("Prod webpack")
    val dir = settingKey[Path]("Directory")
  }

  import autoImport._

  val clientProject = Def.settingDyn(jsProject)

  override def projectSettings: Seq[Setting[_]] = Seq(
    exportJars := false,
    distDirectory := (target.value / "dist").toPath,
    watchSources := watchSources.value ++ Def.taskDyn(watchSources in clientProject.value).value,
    fastWebpack := Def.taskDyn {
      webpack.in(clientProject.value, Compile, fastOptJS in clientProject.value)
    }.value,
    fullWebpack := Def.taskDyn {
      webpack.in(clientProject.value, Compile, fullOptJS in clientProject.value)
    }.value,
    // https://github.com/sbt/sbt/issues/2975#issuecomment-358709526
    build := Def.taskDyn {
      val assets = prepareRelative(fastWebpack.value, distDirectory.value)
      run in Compile toTask s" build ${distDirectory.value} /workbench.js $assets"
    }.value,
    publishLocal in Static := build.value,
    clean in Static := deleteDirectory(distDirectory.value),
    prepare := Def.taskDyn {
      val assets = prepareRelative(fullWebpack.value, distDirectory.value)
      run in Compile toTask s" prepare ${distDirectory.value} $assets"
    }.dependsOn(clean in Static).value,
    deploy := Def.taskDyn {
      run in Compile toTask s" deploy ${distDirectory.value} ${bucket.value}"
    }.dependsOn(prepare).value,
    publish in Static := deploy.value,
    publish := deploy.value
  )

  case class AssetGroup(scripts: Seq[File], styles: Seq[File])

  def prepareRelative(files: Seq[Attributed[File]],
                      base: Path,
                      excludePrefixes: Seq[String] = Seq("styles", "fonts")) = {
    val eligible = assetGroup(files, excludePrefixes)
    val assetsDir = base.toFile / "assets"

    def copyAndRelativize(subDir: String, file: File) = {
      val dest = assetsDir / subDir / file.name
      if (file.getAbsolutePath != dest.getAbsolutePath)
        IO.copyFile(file, dest)
      base.relativize(dest.toPath)
    }

    val relative =
      eligible.styles.map(copyAndRelativize("css", _)) ++
        eligible.scripts.map(copyAndRelativize("js", _))
    relative.map { p =>
      val r = p.toFile.getPath
      if (r.startsWith("/")) r else s"/$r"
    }.mkString(" ")
  }

  def assetGroup(files: Seq[Attributed[File]], excludePrefixes: Seq[String]): AssetGroup = {
    def filesOf(fileType: BundlerFileType) = files.filter(_.metadata.get(BundlerFileTypeAttr).contains(fileType))

    // Orders library scripts before app scripts
    val apps = filesOf(BundlerFileType.Application) ++ filesOf(BundlerFileType.ApplicationBundle)
    val libraries = filesOf(BundlerFileType.Library)
    val assets = filesOf(BundlerFileType.Asset)
    val loaders = filesOf(BundlerFileType.Loader)
    val scripts = (apps ++ loaders ++ assets ++ libraries).distinct.reverse.map(_.data)
      .filter(f => f.ext == "js" && !excludePrefixes.exists(e => f.name.startsWith(e)))
      .distinct
    val styles = files.map(_.data).filter(_.ext == "css")
    AssetGroup(scripts, styles)
  }

  // https://stackoverflow.com/a/27917071
  def deleteDirectory(dir: Path): Path = {
    if (Files.exists(dir)) {
      Files.walkFileTree(dir, new SimpleFileVisitor[Path] {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          Files.delete(file)
          FileVisitResult.CONTINUE
        }

        override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
          Files.delete(dir)
          FileVisitResult.CONTINUE
        }
      })
    } else {
      dir
    }
  }
}
