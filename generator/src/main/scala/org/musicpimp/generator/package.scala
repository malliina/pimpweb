package org.musicpimp

import java.nio.file.{Path, Paths}

import play.api.libs.json.{Format, Json, Reads, Writes}

package object generator {
  implicit val pathFormat: Format[Path] = Format[Path](
    Reads(json => json.validate[String].map(s => Paths.get(s))),
    Writes(p => Json.toJson(p.toAbsolutePath.toString))
  )

  implicit class PathOps(val path: Path) extends AnyVal {
    def extension = PathUtils.ext(path)

    def name = path.getFileName.toString
  }

}

object PathUtils {
  def ext(path: Path) =
    extOf(path.getFileName.toString).getOrElse("")

  def extOf(name: String): Option[String] = {
    val idx = name.lastIndexOf('.')
    if (idx >= 0 && name.length > idx + 1) Option(name.substring(idx + 1)).filter(_.nonEmpty)
    else None
  }

}
