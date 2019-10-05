package com.malliina.generator

import java.nio.file.Path

import com.malliina.PathUtils

object ContentTypes extends ContentTypes

trait ContentTypes {
  val defaultContentType = ContentType("application/octet-stream")

  val contentTypes = Map(
    "html" -> "text/html",
    "json" -> "application/json",
    "js" -> "text/javascript",
    "css" -> "text/css",
    "jpg" -> "image/jpg",
    "png" -> "image/png",
    "gif" -> "image/gif",
    "svg" -> "image/svg+xml",
    "woff" -> "font/woff",
    "woff2" -> "font/woff2",
    "eot" -> "font/eot",
    "ttf" -> "font/ttf",
    "otf" -> "font/otf"
  ).view.mapValues(ContentType.apply)

  def resolve(file: Path) = contentTypes.getOrElse(PathUtils.ext(file), defaultContentType)
}
