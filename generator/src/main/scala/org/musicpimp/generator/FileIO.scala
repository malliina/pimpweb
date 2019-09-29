package org.musicpimp.generator

import java.nio.file.{Files, Path, StandardCopyOption, StandardOpenOption}

import org.slf4j.LoggerFactory
import play.api.libs.json.{Json, Writes}

object FileIO extends FileIO

class FileIO {
  private val log = LoggerFactory.getLogger(getClass)

  def writeJson[T: Writes](t: T, to: Path): Path = write(Json.toBytes(Json.toJson(t)), to)

  def write(page: TagPage, to: Path): Path = write(page.toBytes, to)

  def write(bytes: Array[Byte], to: Path): Path = {
    if (!Files.isRegularFile(to)) {
      val dir = to.getParent
      if (!Files.isDirectory(dir))
        Files.createDirectories(dir)
      Files.createFile(to)
    }
    Files.write(to, bytes, StandardOpenOption.TRUNCATE_EXISTING)
    log.info(s"Wrote ${to.toAbsolutePath}.")
    to
  }

  def copy(from: Path, to: Path): Unit = {
    val dir = to.getParent
    if (!Files.isDirectory(dir))
      Files.createDirectories(dir)
    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
    log.info(s"Copied ${from.toAbsolutePath} to ${to.toAbsolutePath}.")
  }
}
