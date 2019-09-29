package org.musicpimp.generator

import java.nio.file.{Files, Path, StandardOpenOption}

import com.malliina.values.WrappedString
import play.api.libs.json.{JsResult, Json}

case class BucketName(value: String) extends AnyVal with WrappedString

case class MappedAssets(scripts: Seq[FileMapping],
                        adhocScripts: Seq[String],
                        styles: Seq[FileMapping],
                        other: Seq[FileMapping]) {
  def all = scripts ++ styles ++ other
  def site(pages: Seq[PageMapping], other: Seq[ByteMapping]) = CompleteSite(this, pages, other)
  def withOther(otherFiles: Seq[FileMapping]) = MappedAssets(scripts, adhocScripts, styles, other ++ otherFiles)
}

case class CompleteSite(assets: MappedAssets, pages: Seq[PageMapping], bytes: Seq[ByteMapping]) {

  /** Writes the site to `base`.
    *
    * @return website files
    */
  def write(base: Path): BuiltSite = {
    val pageFiles = pages.map { page =>
      val pageFile = FileIO.write(page.page, base / page.to)
      WebsiteFile(pageFile, page.to)
    }
    val assetFiles = assets.all.map { a =>
      val assetFile = base / a.relative
      FileIO.copy(a.from, assetFile)
      WebsiteFile(assetFile, a.relative)
    }
    val otherFiles = bytes.map { byteMapping =>
      val out = FileIO.write(byteMapping.bytes, base / byteMapping.to)
      WebsiteFile(out, byteMapping.to)
    }

    BuiltSite(pageFiles ++ assetFiles ++ otherFiles)
  }
}

/**
  * @param from local file
  * @param to   relative to the target directory
  */
case class FileMapping(from: Path, to: String) {
  def relative = if (to.startsWith("/")) to.drop(1) else to
}

object FileMapping {
  implicit val json = Json.format[FileMapping]
}

case class PageMapping(page: TagPage, to: String)
case class ByteMapping(bytes: Array[Byte], to: String)

case class AssetGroup(scripts: Seq[Path], styles: Seq[Path], statics: Seq[Path])

object AssetGroup {
  implicit val json = Json.format[AssetGroup]
}

case class AssetsManifest(scripts: Seq[Path],
                          adhocScripts: Seq[String],
                          styles: Seq[Path],
                          statics: Seq[Path],
                          assetsBase: Path)

object AssetsManifest {
  implicit val json = Json.format[AssetsManifest]

  def apply(file: Path): JsResult[AssetsManifest] =
    Json.parse(Files.readAllBytes(file)).validate[AssetsManifest]
}

case class SiteManifest(css: Seq[String], js: Seq[String], statics: Seq[String], targetDirectory: Path) {
  def to(file: Path): Path =
    Files.write(file, Json.toBytes(SiteManifest.json.writes(this)), StandardOpenOption.CREATE)
}

object SiteManifest {
  implicit val json = Json.format[SiteManifest]

  def apply(file: Path): JsResult[SiteManifest] =
    Json.parse(Files.readAllBytes(file)).validate[SiteManifest]
}

case class SiteSpec(css: Seq[FileMapping],
                    js: Seq[FileMapping],
                    assets: Seq[FileMapping],
                    statics: Seq[FileMapping],
                    targetDirectory: Path,
                    routes: Routes) {
  def all = css ++ js ++ assets ++ statics
}

/**
  * @param files files written
  */
case class BuiltSite(files: Seq[WebsiteFile])

object BuiltSite {
  implicit val json = Json.format[BuiltSite]
}

case class VersionInfo(name: String, version: String, gitHash: String)

object VersionInfo {
  implicit val json = Json.format[VersionInfo]

  val default: VersionInfo = VersionInfo(BuildInfo.name, BuildInfo.version, BuildInfo.gitHash)
}
