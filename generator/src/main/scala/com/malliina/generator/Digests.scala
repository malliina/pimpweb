package com.malliina.generator

import java.nio.file.{Files, Path}

import com.malliina.PathUtils
import com.malliina.values.{StringCompanion, WrappedString}
import org.apache.commons.codec.digest.DigestUtils
import scalatags.Text.all._

import scala.collection.concurrent.TrieMap
import scala.language.implicitConversions

case class AssetPath(value: String) extends AnyVal with WrappedString {
  override def toString = value
  def startsWith(s: String) = value.startsWith(s)
  def drop(n: Int) = AssetPath(value.drop(n))
  def ext = PathUtils.extOf(value).getOrElse("")
  def name = value.dropRight(ext.length + 1)
}

object AssetPath extends StringCompanion[AssetPath] {
  implicit val v: AttrValue[AssetPath] = HTML.attrValue(_.value)

  implicit def fromString(s: String): AssetPath = apply(s)
}

case class Hash(value: String) extends AnyVal {
  override def toString = value
}

case class DigestedFile(original: Path, hash: Hash)

object Digests extends Digests

class Digests {
  def compute(original: Path): DigestedFile = DigestedFile(original, hash(original))

  def hash(file: Path): Hash =
    Hash(DigestUtils.md5Hex(Files.readAllBytes(file)))
}

class DigestFinder(digest: Digests) extends AssetFinder {
  val cache = TrieMap.empty[AssetPath, AssetPath]

  def findDigested(path: AssetPath): Option[AssetPath] = cache.get(path)

  def digestedPath(path: AssetPath, hash: Hash): AssetPath =
    cache.getOrElseUpdate(path, AssetPath(s"${path.name}-$hash.${path.ext}"))
}

trait AssetFinder {
  def path(to: AssetPath): AssetPath = findDigested(to).getOrElse(to)
  def findDigested(path: AssetPath): Option[AssetPath]
  protected def digestedPath(path: AssetPath, hash: Hash): AssetPath
}
