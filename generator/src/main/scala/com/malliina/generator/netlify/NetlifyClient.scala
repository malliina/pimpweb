package com.malliina.generator.netlify

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import com.malliina.generator.{BuiltSite, CompleteSite, FileIO, IO, WebsiteFile}

case class NetlifyHeader(path: String, headers: Map[String, String]) {
  // https://docs.netlify.com/routing/headers/#syntax-for-the-headers-file
  def asString: String = {
    val headerList = headers.map { case (k, v) => s"$k: $v" }.mkString("\n  ", "\n  ", "\n")
    s"$path$headerList"
  }
}

object NetlifyHeader {
  def forall(headers: Map[String, String]) = apply("/*", headers)

  def security = forall(Map("X-Frame-Options" -> "DENY", "X-XSS-Protection" -> "1; mode=block"))
}

object NetlifyClient extends NetlifyClient

class NetlifyClient {
  def build(site: CompleteSite, to: Path): BuiltSite = {
    val content = site.write(to)
    writeHeadersFile(content.files, to.resolve("_headers"))
    content
  }

  def deploy(site: BuiltSite) = {
    sys.env.foreach { case (k, v) => println(s"$k=$v") }
    IO.run("netlify deploy --prod")
  }

  private def writeHeadersFile(files: Seq[WebsiteFile], to: Path): Path = {
    val netlifyHeaders = NetlifyHeader.security +: files.map { file =>
      NetlifyHeader(
        s"/${file.key.value}",
        Map("Cache-Control" -> file.cacheControl.value, "Content-Type" -> file.contentType.value)
      )
    }
    FileIO.write(netlifyHeaders.map(_.asString).mkString.getBytes(StandardCharsets.UTF_8), to)
  }
}
