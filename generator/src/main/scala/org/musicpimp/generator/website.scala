package org.musicpimp.generator

import java.nio.file.{Files, Path}

import com.malliina.values.{StringCompanion, WrappedString}
import org.musicpimp.PathUtils
import play.api.libs.json.Json

import scala.jdk.CollectionConverters.IteratorHasAsScala

case class StorageKey(value: String) extends AnyVal with WrappedString {
  def startsWith(s: String) = value.startsWith(s)
}
object StorageKey extends StringCompanion[StorageKey] {
  def apply(relativeFile: Path, stripExt: Boolean): StorageKey =
    apply(relativeFile.toString.replace('\\', '/'), stripExt)

  def apply(relativePath: String, stripExt: Boolean): StorageKey = {
    val chopped =
      if (stripExt)
        PathUtils
          .extOf(relativePath)
          .map(ext => relativePath.dropRight(ext.length + 1))
          .getOrElse(relativePath)
      else
        relativePath
    StorageKey(if (chopped.startsWith("/")) chopped.drop(1) else chopped)
  }
}

case class CacheControl(value: String) extends AnyVal with WrappedString
object CacheControl extends StringCompanion[CacheControl]

case class ContentType(value: String) extends AnyVal with WrappedString
object ContentType extends StringCompanion[ContentType]

case class WebsiteFile(file: Path, key: StorageKey, contentType: ContentType, cacheControl: CacheControl) {
  def ext = PathUtils.ext(file)
  def name = file.getFileName.toString
}

object WebsiteFile {
  implicit val json = Json.format[WebsiteFile]

  val htmlExt = ".html"

  def apply(file: Path, path: String): WebsiteFile = {
    val key = StorageKey(path, path.endsWith(htmlExt))
    WebsiteFile(file, key, ContentTypes.resolve(file), CacheControls.compute(file, key))
  }

  def list(dir: Path, cacheControls: CacheControls) =
    Files.walk(dir).iterator().asScala.toList.filter(p => Files.isRegularFile(p)).map { file =>
      val relativePath = dir.relativize(file)
      val key = StorageKey(relativePath, relativePath.name.endsWith(htmlExt))
      val cacheControl = cacheControls.compute(file, key)
      WebsiteFile(file, key, ContentTypes.resolve(file), cacheControl)
    }
}

case class Website(indexKey: StorageKey, notFoundKey: StorageKey, files: BuiltSite)

object Website {
  implicit val json = Json.format[Website]

  val defaultIndexFile = StorageKey("index")
  val defaultNotFoundFile = StorageKey("404")
}
