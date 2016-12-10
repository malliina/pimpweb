package com.malliina.tags

import scalatags.Text.TypedTag
import scalatags.Text.all._

object Tags extends Tags

trait Tags {
  def divClass(clazz: String) = div(`class` := clazz)

  def spanClass(clazz: String) = span(`class` := clazz)

  def iClass(clazz: String) = i(`class` := clazz)

  def leadPara = pClass("lead")

  def pClass(clazz: String) = p(`class` := clazz)

  def ulClass(clazz: String) = ul(`class` := clazz)

  def liHref(url: String, text: String) = li(aHref(url, text))

  def aHref(url: String): TypedTag[String] = aHref(url, url)

  def aHref[V: AttrValue](url: V, text: String): TypedTag[String] = a(href := url)(text)

  def js[V: AttrValue](url: V) = script(src := url)

  def cssLink[V: AttrValue](url: V) = link(rel := "stylesheet", href := url)
}
