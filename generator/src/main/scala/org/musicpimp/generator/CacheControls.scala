package org.musicpimp.generator

import java.nio.file.Path

import org.musicpimp.PathUtils

object CacheControls extends CacheControls

trait CacheControls {
  val defaultCacheControl = CacheControl("public, max-age=60")
  val eternalCache = CacheControl("public, max-age=31536000")
  val cacheControls = Map(
    "js" -> eternalCache,
    "css" -> eternalCache,
    "jpg" -> eternalCache,
    "png" -> eternalCache,
    "svg" -> eternalCache,
    "html" -> CacheControl("public, max-age=60")
  )

  def compute(file: Path, key: StorageKey): CacheControl = {
    val name = file.getFileName.toString
    val isFingerprinted = name.count(_ == '.') > 1
    if (key.startsWith("assets/static")) eternalCache
    else if (isFingerprinted) cacheControls.getOrElse(PathUtils.ext(file), defaultCacheControl)
    else defaultCacheControl
  }
}
