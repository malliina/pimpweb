package controllers

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet

import scala.io.Source

object Docs extends Docs

trait Docs {
  val lineSep = sys.props("line.separator")

  def fromFile(file: String) = toHtml(markdownAsString(file))

  def markdownAsString(docName: String): String =
    Source.fromResource(s"docs/$docName.md").getLines().toList.mkString(lineSep)

  def toHtml(markdownSource: String): String = {
    val options = new MutableDataSet()
    val parser = Parser.builder(options).build()
    val renderer = HtmlRenderer.builder(options).build()
    val doc = parser.parse(markdownSource)
    renderer.render(doc)
  }
}
