package com.malliina.pimpweb

import com.malliina.pimpweb.html.PimpWebHtml
import org.scalatest.selenium.HtmlUnit
import tests.ServerSuite

class AppTests extends ServerSuite(ctx => new AppComponents(ctx)) with HtmlUnit {
  val host = s"http://localhost:$port"

  test("selenium demo") {
    go to host
    assert(pageTitle === "MusicPimp")
    val header = cssSelector(".hero-section h2").findElement
    assert(header.isDefined)
    assert(header.get.text == PimpWebHtml.subHeader)
  }
}
