package com.malliina.generator

import java.nio.file.Path
import com.malliina.PathUtils

object CacheControls extends CacheControls

trait CacheControls {
  val defaultCacheControl = CacheControl("public, max-age=60")
  val eternalCache = CacheControl("public, max-age=31536000")
  val eternallyCached = Seq("js", "css", "jpg", "png", "svg", "woff", "woff2", "ttf", "svg", "eot")
  val eternalControls = eternallyCached.map { k =>
    k -> eternalCache
  }.toMap
  val cacheControls = eternalControls ++ Map(
    "html" -> CacheControl("public, max-age=60")
  )

  def compute(file: Path, key: StorageKey): CacheControl = {
    val name = file.getFileName.toString
    compute(file, name.count(_ == '.') > 1)
  }

  def compute(file: Path, isFingerprinted: Boolean): CacheControl = {
    if (isFingerprinted) cacheControls.getOrElse(PathUtils.ext(file), defaultCacheControl)
    else defaultCacheControl
  }
}
