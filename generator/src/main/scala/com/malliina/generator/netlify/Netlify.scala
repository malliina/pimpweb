package com.malliina.generator.netlify

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import com.malliina.generator.{FileIO, WebsiteFile}

case class NetlifyHeader(path: String, headers: Map[String, String]) {
  def asString: String = {
    val headerList = headers.map { case (k, v) => s"$k: $v" }.mkString("\n  ", "\n  ", "\n")
    s"$path$headerList"
  }
}

object NetlifyHeader {
  def forall(headers: Map[String, String]) = apply("/*", headers)

  def security = forall(Map("X-Frame-Options" -> "DENY", "X-XSS-Protection" -> "1; mode=block"))
}

object Netlify extends Netlify

class Netlify {
  def headers(files: Seq[WebsiteFile], to: Path): Path = {
    val netlifyHeaders = NetlifyHeader.security +: files.map { file =>
      NetlifyHeader(
        s"/${file.key.value}",
        Map("Cache-Control" -> file.cacheControl.value, "Content-Type" -> file.contentType.value)
      )
    }
    FileIO.write(netlifyHeaders.map(_.asString).mkString.getBytes(StandardCharsets.UTF_8), to)
  }
}
