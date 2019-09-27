package com.malliina.pimpweb.js

import com.malliina.html.Tags
import com.malliina.http.FullUrl
import com.malliina.pimpweb.{Downloads, FrontKeys}
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.Element
import scalatags.JsDom.all._
import scalatags.generic

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object JSTags extends Tags(scalatags.JsDom)

class DownloadsPage extends com.malliina.html.Bootstrap(JSTags) {

  import tags._

  val bucketName = "files.musicpimp.org"
  val prefix = "musicpimp"

  val listObjectsUrl = s"https://www.googleapis.com/storage/v1/b/$bucketName/o"
  val downloadBaseUrl = FullUrl.https(bucketName, "")

  Ajax.get(listObjectsUrl).map { xhr =>
    xhr
      .validate[ListObjectsResponse]
      .map { res =>
        appendHistorical(
          res.items
            .map(_.name)
            .filterNot(name => Downloads.latestDownloads.exists(_.fileName == name))
            .sorted
            .reverse)
      }
      .left
      .foreach { err =>
        println(err)
      }
  }

  implicit val v: generic.AttrValue[Element, FullUrl] =
    (t: Element, a: Attr, v: FullUrl) => t.setAttribute(a.name, v.url)

  def appendHistorical(files: Seq[String]): Unit = {
    Option(dom.document.getElementById(FrontKeys.PimpListId)).foreach { ul =>
      files.map { file =>
        ul.appendChild(liHref(downloadBaseUrl / file)(file).render)
      }
    }
  }
}
