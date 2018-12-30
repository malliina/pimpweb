package com.malliina.pimpweb.js

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object PimpWeb {
  def main(args: Array[String]): Unit = {
    val isDownloadsPage = dom.document.body.classList.contains("downloads")
    if (isDownloadsPage) {
      new Downloads
    }
    touchBundles()
  }

  def touchBundles(): Unit = {
    /** To bundle the JS libraries (defined in key npmDependencies in build.sbt) into the built js file,
      * a facade with @JSImport like below is needed for each library. Also, they must be referenced in code.
      *
      * So, these are the empty facades and references to them. The fact they're empty objects and otherwise
      * unused in application code is irrelevant.
      *
      * See https://scalacenter.github.io/scalajs-bundler/reference.html#npm-dependencies for details.
      */
    val j = JQueryDummy
    val p = Popper
    val b = Bootstrap
  }
}

@js.native
@JSImport("jquery", JSImport.Namespace)
object JQueryDummy extends js.Object

@js.native
@JSImport("popper.js", JSImport.Namespace)
object Popper extends js.Object

@js.native
@JSImport("bootstrap", JSImport.Namespace)
object Bootstrap extends js.Object
