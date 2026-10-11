package com.malliina.pimpweb.js

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object WebApp:
  val p = Popper
  val b = Bootstrap
  val bootstrapCss = BootstrapCss
  val css = AppCss

  def main(args: Array[String]): Unit =
    val isDownloadsPage = dom.document.body.classList.contains("downloads")
    if isDownloadsPage then new DownloadsPage

@js.native
@JSImport("@popperjs/core", JSImport.Namespace)
object Popper extends js.Object

@js.native
@JSImport("bootstrap", JSImport.Namespace)
object Bootstrap extends js.Object

@js.native
@JSImport("./css/app", JSImport.Namespace)
object AppCss extends js.Object

@js.native
@JSImport("bootstrap/dist/css/bootstrap.min.css", JSImport.Namespace)
object BootstrapCss extends js.Object
