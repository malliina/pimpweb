package controllers

import java.nio.file.Path

import com.malliina.file.FileUtilities
import org.pegdown.PegDownProcessor
import play.twirl.api.{Html, HtmlFormat}

object Docs extends Docs

trait Docs {
  def fromFile(file: Path): Option[Html] = {
    val fileAsString = FileUtilities.fileToString(file)
    fromString(fileAsString)
  }

  def fromString(markdownSource: String): Option[Html] = {
    // local scope as PegDownProcessor is not thread-safe
    val pdp = new PegDownProcessor()
    val out = Option(pdp.markdownToHtml(markdownSource))
    out.map(HtmlFormat.raw)
  }
}
