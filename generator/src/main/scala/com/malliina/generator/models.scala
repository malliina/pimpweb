package com.malliina.generator

import java.nio.file.{Files, Path, StandardOpenOption}
import com.malliina.values.{ErrorMessage, StringCompanion, StringEnumCompanion, WrappedString}
import io.circe.{Codec, Encoder, Json}
import scalatags.Text.all.AttrValue

import java.nio.charset.StandardCharsets

sealed abstract class Command(val name: String)

object Command extends StringEnumCompanion[Command]:
  override def all = Seq(Build, Deploy)
  override def write(t: Command): String = t.name

  case object Build extends Command("build")
  case object Deploy extends Command("deploy")

case class BuildSpec(cmd: Command, manifest: Path) derives Codec.AsObject

case class CacheControl(value: String) extends AnyVal with WrappedString
object CacheControl extends StringCompanion[CacheControl]:
  override def build(input: String): Either[ErrorMessage, CacheControl] = Right(apply(input))

case class ContentType(value: String) extends AnyVal with WrappedString
object ContentType extends StringCompanion[ContentType]:
  override def build(input: String): Either[ErrorMessage, ContentType] = Right(apply(input))

case class BucketName(value: String) extends AnyVal with WrappedString
object BucketName extends StringCompanion[BucketName]:
  override def build(input: String): Either[ErrorMessage, BucketName] = Right(apply(input))

case class MappedAssets(
  scripts: Seq[FileMapping],
  adhocScripts: Seq[AssetPath],
  styles: Seq[FileMapping],
  other: Seq[FileMapping]
):
  def all = scripts ++ styles ++ other
  def site(
    pages: Seq[PageMapping],
    other: Seq[ByteMapping],
    index: StorageKey,
    notFound: StorageKey
  ) = CompleteSite(this, pages, other, index, notFound)
  def withOther(otherFiles: Seq[FileMapping]) =
    MappedAssets(scripts, adhocScripts, styles, other ++ otherFiles)

case class CompleteSite(
  assets: MappedAssets,
  pages: Seq[PageMapping],
  bytes: Seq[ByteMapping],
  index: StorageKey,
  notFound: StorageKey
):

  /** Writes the site to `base`.
    *
    * @return
    *   website files
    */
  def write(base: Path): BuiltSite =
    val pageFiles = pages.map: page =>
      val pageFile = FileIO.write(page.page, base / page.to)
      WebsiteFile(pageFile, page.to)
    val assetFiles = assets.all.map: a =>
      val assetFile = base / a.relative.value
      FileIO.copy(a.from, assetFile)
      WebsiteFile(a)
    val otherFiles = bytes.map: byteMapping =>
      val out = FileIO.write(byteMapping.bytes, base / byteMapping.to)
      WebsiteFile(out, byteMapping.to)

    BuiltSite(pageFiles ++ assetFiles ++ otherFiles, index, notFound)

/** @param from
  *   local file
  * @param to
  *   relative to the target directory
  */
case class FileMapping(from: Path, to: AssetPath, isFingerprinted: Boolean) derives Codec.AsObject:
  def relative = if to.startsWith("/") then to.drop(1) else to

case class PageMapping(page: TagPage, to: String)
case class ByteMapping(bytes: Array[Byte], to: String)

case class AssetGroup(scripts: Seq[Path], styles: Seq[Path], statics: Seq[Path])
  derives Codec.AsObject

case class AssetsManifest(
  scripts: Seq[Path],
  adhocScripts: Seq[AssetPath],
  styles: Seq[Path],
  statics: Seq[Path],
  assetsBase: Path
) derives Codec.AsObject

object AssetsManifest:
  def apply(file: Path): Either[io.circe.Error, AssetsManifest] =
    io.circe.parser.decode[AssetsManifest](Files.readString(file))

case class SiteManifest(
  css: Seq[String],
  js: Seq[String],
  statics: Seq[String],
  targetDirectory: Path
) derives Codec.AsObject:
  def to(file: Path): Path =
    Files.write(
      file,
      Encoder[SiteManifest].apply(this).spaces2.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE
    )

object SiteManifest:
  def apply(file: Path): Either[io.circe.Error, SiteManifest] =
    io.circe.parser.decode[SiteManifest](Files.readString(file))

/** @param files
  *   files written
  */
case class BuiltSite(files: Seq[WebsiteFile], index: StorageKey, notFound: StorageKey)
  derives Codec.AsObject

object BuiltSite:
  val defaultIndexFile = StorageKey("index")
  val defaultNotFoundFile = StorageKey("404")

case class VersionInfo(name: String, version: String, gitHash: String) derives Codec.AsObject

object VersionInfo:
  val default: VersionInfo = VersionInfo(BuildInfo.name, BuildInfo.version, BuildInfo.gitHash)

case class Route(name: StorageKey, file: String, uri: String)

object Route:
  def apply(name: String): Route = Route(StorageKey(name), s"$name.html", s"/$name")
  def local(name: String): Route = Route(StorageKey(name), s"$name.html", s"$name.html")
  def localJson(name: String): Route = Route(StorageKey(name), s"$name.json", s"/$name.json")
  def simple(name: String): Route = Route(StorageKey(name), name, s"/$name")

  implicit val v: AttrValue[Route] = HTML.attrValue(_.uri)

sealed trait AppMode

object AppMode:
  case object Dev extends AppMode
  case object Prod extends AppMode

case class BuiltPages(pages: Seq[PageMapping], index: StorageKey, notFound: StorageKey)

case class ExitValue(value: Int) extends AnyVal:
  override def toString = s"$value"
