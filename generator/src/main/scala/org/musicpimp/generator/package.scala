package org.musicpimp

import java.nio.file.Path

package object generator {

  implicit class PathOps(val path: Path) extends AnyVal {
    def extension = PathUtils.ext(path)

    def name = path.getFileName.toString
  }


}

object PathUtils {
  def ext(path: Path) = {
    val name = path.getFileName.toString
    val idx = name.lastIndexOf('.')
    if (idx >= 0 && name.length > idx + 1) name.substring(idx + 1)
    else ""
  }
}
