package org.musicpimp

import java.nio.file.Path

import play.api.libs.json.Format

package object generator {
  implicit val pathFormat: Format[Path] = Formats.pathFormat

  implicit class PathOps(val path: Path) extends AnyVal {
    def extension = PathUtils.ext(path)
    def name = path.getFileName.toString
    def /(name: String): Path = path.resolve(name)
    def /(name: Path): Path = path.resolve(name)
  }

}

object PathUtils {
  def ext(path: Path) =
    extOf(path.getFileName.toString).getOrElse("")

  /** The extension, excluding the dot.
    *
    * "king.jpg" returns "jpg"
    */
  def extOf(name: String): Option[String] = {
    val idx = name.lastIndexOf('.')
    if (idx >= 0 && name.length > idx + 1) Option(name.substring(idx + 1)).filter(_.nonEmpty)
    else None
  }

}
