package com.malliina.generator

import java.nio.file.{Files, Path}
import com.malliina.PathUtils
import com.malliina.values.{ErrorMessage, StringCompanion, WrappedString}
import io.circe.Codec

import scala.jdk.CollectionConverters.IteratorHasAsScala

case class StorageKey(value: String) extends AnyVal with WrappedString:
  def startsWith(s: String) = value.startsWith(s)

object StorageKey extends StringCompanion[StorageKey]:
  override def build(input: String): Either[ErrorMessage, StorageKey] = Right(apply(input))

  def apply(relativeFile: Path, stripExt: Boolean): StorageKey =
    apply(relativeFile.toString.replace('\\', '/'), stripExt)

  def apply(relativePath: String, stripExt: Boolean): StorageKey =
    val chopped =
      if stripExt then
        PathUtils
          .extOf(relativePath)
          .map(ext => relativePath.dropRight(ext.length + 1))
          .getOrElse(relativePath)
      else relativePath
    StorageKey(if chopped.startsWith("/") then chopped.drop(1) else chopped)

case class WebsiteFile(
  file: Path,
  key: StorageKey,
  contentType: ContentType,
  cacheControl: CacheControl
) derives Codec.AsObject:
  def ext = PathUtils.ext(file)
  def name = file.getFileName.toString

object WebsiteFile:
  val htmlExt = ".html"

  def apply(file: Path, path: String): WebsiteFile =
    val key = StorageKey(path, path.endsWith(htmlExt))
    WebsiteFile(file, key, ContentTypes.resolve(file), CacheControls.compute(file, key))

  def apply(mapping: FileMapping): WebsiteFile =
    val key = StorageKey(mapping.relative.value, mapping.relative.value.endsWith(htmlExt))
    WebsiteFile(
      mapping.from,
      key,
      ContentTypes.resolve(mapping.from),
      CacheControls.compute(mapping.from, mapping.isFingerprinted)
    )

  def list(dir: Path, cacheControls: CacheControls): List[WebsiteFile] =
    Files
      .walk(dir)
      .iterator()
      .asScala
      .toList
      .filter(p => Files.isRegularFile(p))
      .map: file =>
        val relativePath = dir.relativize(file)
        val key = StorageKey(relativePath, relativePath.name.endsWith(htmlExt))
        val cacheControl = cacheControls.compute(file, key)
        WebsiteFile(file, key, ContentTypes.resolve(file), cacheControl)
