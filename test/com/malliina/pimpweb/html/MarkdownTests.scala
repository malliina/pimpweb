package com.malliina.pimpweb.html

import org.scalatest.FunSuite

class MarkdownTests extends FunSuite {
  test("can parse markdown") {
    assert(PimpWebHtml.api.tags.modifiers.nonEmpty)
  }
}
