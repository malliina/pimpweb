import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.{BundlerFileType, BundlerFileTypeAttr}

case class AssetGroup(scripts: Seq[String], styles: Seq[String])

object AssetHelper {

  def assetGroup(files: Seq[sbt.Attributed[sbt.File]], excludePrefixes: Seq[String]): AssetGroup = {
    def filesOf(fileType: BundlerFileType) = files.filter(_.metadata.get(BundlerFileTypeAttr).contains(fileType))

    // Orders library scripts before app scripts
    val apps = filesOf(BundlerFileType.Application) ++ filesOf(BundlerFileType.ApplicationBundle)
    val libraries = filesOf(BundlerFileType.Library)
    val assets = filesOf(BundlerFileType.Asset)
    val loaders = filesOf(BundlerFileType.Loader)
    val scripts = (apps ++ loaders ++ assets ++ libraries).distinct.reverse.map(_.data)
      .filter(f => f.ext == "js" && !excludePrefixes.exists(e => f.name.startsWith(e)))
      .map(_.name).distinct
    val styles = files.map(_.data).filter(_.ext == "css").map(_.name)
    AssetGroup(scripts, styles)
  }
}
