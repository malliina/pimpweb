package com.malliina.tags

import scalatags.Text.TypedTag
import scalatags.Text.all._

object Tags extends Tags

trait Tags {
  val crossorigin = attr("crossorigin")
  val integrity = attr("integrity")
  val Anonymous = "anonymous"

  def divClass(clazz: String) = div(`class` := clazz)

  def spanClass(clazz: String, more: Modifier*) = span(`class` := clazz, more)

  def iClass(clazz: String) = i(`class` := clazz)

  def leadPara = pClass("lead")

  def pClass(clazz: String) = p(`class` := clazz)

  def ulClass(clazz: String) = ul(`class` := clazz)

  def liHref(url: String, text: String) = li(aHref(url, text))

  def aHref(url: String): TypedTag[String] = aHref(url, url)

  def aHref[V: AttrValue](url: V, text: String): TypedTag[String] =
    a(href := url)(text)

  def jsHashed[V: AttrValue](url: V, integrityHash: String, more: Modifier*) =
    script(src := url, integrity := integrityHash, crossorigin := Anonymous, more)

  def js[V: AttrValue](url: V, more: Modifier*) = script(src := url, more)

  def cssLinkHashed[V: AttrValue](url: V, integrityHash: String, more: Modifier*) =
    cssLink(url, integrity := integrityHash, crossorigin := Anonymous, more)

  def cssLink[V: AttrValue](url: V, more: Modifier*) =
    link(rel := "stylesheet", href := url, more)
}
