package controllers

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet

object Docs extends Docs

trait Docs {
  /**
    * @param markdownSource markdown
    * @return HTML
    */
  def toHtml(markdownSource: String): String = {
    val options = new MutableDataSet()
    val parser = Parser.builder(options).build()
    val renderer = HtmlRenderer.builder(options).build()
    val doc = parser.parse(markdownSource)
    renderer.render(doc)
  }
}
