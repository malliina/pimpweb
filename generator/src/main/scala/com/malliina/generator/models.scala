package com.malliina.generator

import java.nio.file.{Files, Path, StandardOpenOption}

import com.malliina.values.{StringCompanion, StringEnumCompanion, WrappedString}
import play.api.libs.json.{Format, JsError, JsResult, JsSuccess, Json}
import scalatags.Text.all.AttrValue

sealed abstract class Command(val name: String)

object Command extends StringEnumCompanion[Command] {
  override def all = Seq(Build, Deploy)
  override def write(t: Command): String = t.name

  case object Build extends Command("build")
  case object Deploy extends Command("deploy")
}

sealed abstract class DeployTarget(val name: String)

object DeployTarget {
  val ServiceKey = "service"

  implicit val json: Format[DeployTarget] = Format[DeployTarget](
    json =>
      (json \ ServiceKey).validate[String].flatMap {
        case NetlifyTarget.name => JsSuccess(NetlifyTarget)
        case GCP.name           => GCP.gcpJson.reads(json)
        case GitHubTarget.name  => GitHubTarget.ghJson.reads(json)
        case other              => JsError(s"Unknown service: '$other'.")
      }, {
      case NetlifyTarget =>
        Json.obj(ServiceKey -> NetlifyTarget.name)
      case gcp @ GCPTarget(_) =>
        Json.obj(ServiceKey -> GCP.name) ++ GCP.gcpJson.writes(gcp)
      case gh @ GitHubTarget(_) =>
        Json.obj(ServiceKey -> GitHubTarget.name) ++ GitHubTarget.ghJson.writes(gh)
    }
  )
  case class GCPTarget(bucket: BucketName) extends DeployTarget(GCP.name)
  object GCP {
    val name = "gcp"
    val gcpJson = Json.format[GCPTarget]
  }
  case object NetlifyTarget extends DeployTarget("netlify")
  case class GitHubTarget(cname: String) extends DeployTarget(GitHubTarget.name)
  object GitHubTarget {
    val name = "github"
    val ghJson = Json.format[GitHubTarget]
  }
}

case class BuildSpec(cmd: Command, manifest: Path, target: DeployTarget)

object BuildSpec {
  implicit val json = Json.format[BuildSpec]
}

case class CacheControl(value: String) extends AnyVal with WrappedString
object CacheControl extends StringCompanion[CacheControl]

case class ContentType(value: String) extends AnyVal with WrappedString
object ContentType extends StringCompanion[ContentType]

case class BucketName(value: String) extends AnyVal with WrappedString
object BucketName extends StringCompanion[BucketName]

case class MappedAssets(
    scripts: Seq[FileMapping],
    adhocScripts: Seq[AssetPath],
    styles: Seq[FileMapping],
    other: Seq[FileMapping]
) {
  def all = scripts ++ styles ++ other
  def site(
      pages: Seq[PageMapping],
      other: Seq[ByteMapping],
      index: StorageKey,
      notFound: StorageKey
  ) = CompleteSite(this, pages, other, index, notFound)
  def withOther(otherFiles: Seq[FileMapping]) = MappedAssets(scripts, adhocScripts, styles, other ++ otherFiles)
}

case class CompleteSite(
    assets: MappedAssets,
    pages: Seq[PageMapping],
    bytes: Seq[ByteMapping],
    index: StorageKey,
    notFound: StorageKey
) {

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
      val assetFile = base / a.relative.value
      FileIO.copy(a.from, assetFile)
      WebsiteFile(a)
    }
    val otherFiles = bytes.map { byteMapping =>
      val out = FileIO.write(byteMapping.bytes, base / byteMapping.to)
      WebsiteFile(out, byteMapping.to)
    }

    BuiltSite(pageFiles ++ assetFiles ++ otherFiles, index, notFound)
  }
}

/**
  * @param from local file
  * @param to   relative to the target directory
  */
case class FileMapping(from: Path, to: AssetPath, isFingerprinted: Boolean) {
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

case class AssetsManifest(
    scripts: Seq[Path],
    adhocScripts: Seq[AssetPath],
    styles: Seq[Path],
    statics: Seq[Path],
    assetsBase: Path
)

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

/**
  * @param files files written
  */
case class BuiltSite(files: Seq[WebsiteFile], index: StorageKey, notFound: StorageKey)

object BuiltSite {
  implicit val json = Json.format[BuiltSite]

  val defaultIndexFile = StorageKey("index")
  val defaultNotFoundFile = StorageKey("404")
}

case class VersionInfo(name: String, version: String, gitHash: String)

object VersionInfo {
  implicit val json = Json.format[VersionInfo]

  val default: VersionInfo = VersionInfo(BuildInfo.name, BuildInfo.version, BuildInfo.gitHash)
}

case class Route(name: StorageKey, file: String, uri: String)

object Route {
  def apply(name: String): Route = Route(StorageKey(name), s"$name.html", s"/$name")
  def local(name: String): Route = Route(StorageKey(name), s"$name.html", s"/$name.html")
  def simple(name: String): Route = Route(StorageKey(name), name, s"/$name")

  implicit val v: AttrValue[Route] = HTML.attrValue(_.uri)
}

sealed trait AppMode

object AppMode {
  case object Dev extends AppMode
  case object Prod extends AppMode
}

case class BuiltPages(pages: Seq[PageMapping], index: StorageKey, notFound: StorageKey)

case class ExitValue(value: Int) extends AnyVal {
  override def toString = s"$value"
}
