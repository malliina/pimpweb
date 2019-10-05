package com.malliina.generator

import scalatags.Text
import scalatags.Text.all._
import scalatags.text.Builder

object HTML extends HTML

trait HTML {
  val titleTag = tag("title")
  val defer = attr("defer").empty

  def aHref(url: String): Modifier = aHref(url, url)
  def aHref[V: AttrValue](url: V, text: String): Modifier = a(href := url)(text)

  def attrValue[T](f: T => String): AttrValue[T] =
    (t: Builder, a: Text.Attr, v: T) => t.setAttr(a.name, Builder.GenericAttrValueSource(f(v)))
}
