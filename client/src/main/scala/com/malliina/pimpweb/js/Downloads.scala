package com.malliina.pimpweb.js

import com.malliina.html.{Bootstrap, Tags}
import com.malliina.http.FullUrl
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.Element
import play.api.libs.json.Json
import scalatags.JsDom.all._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object JSTags extends Tags(scalatags.JsDom)

object Downloads {
  val winVersion = "3.10.0"
  val debVersion = "3.10.7"
  val rpmVersion = "3.6.3"
  val macVersion = "3.10.0"
  val msiDownload = s"musicpimp-$winVersion.msi"
  val debDownload = s"musicpimp_${debVersion}_all.deb"
  val rpmDownload = s"musicpimp-$rpmVersion-1.noarch.rpm"
  val dmgDownload = s"musicpimp-$macVersion.dmg"
  val latest = Seq(msiDownload, debDownload, rpmDownload, dmgDownload)
}

class Downloads extends Bootstrap(JSTags) {
  import tags._

  val bucketName = "files.musicpimp.org"
  val prefix = "musicpimp"
  val listObjectsUrl = s"https://www.googleapis.com/storage/v1/b/$bucketName/o"
  val downloadBaseUrl = FullUrl.https(bucketName, "")

  Ajax.get(listObjectsUrl).map { xhr =>
    Json.parse(xhr.responseText).validate[ListObjectsResponse].asEither.fold(
      err => println(err),
      res => appendHistorical(res.items.map(_.name)
        .filterNot(i => Downloads.latest.contains(i))
        .sorted
        .reverse)
    )
  }

  implicit val v = new AttrValue[FullUrl] {
    override def apply(t: Element, a: Attr, v: FullUrl): Unit = t.setAttribute(a.name, v.url)
  }

  def appendHistorical(files: Seq[String]): Unit = {
    dom.document.getElementsByClassName("pimp-list").headOption.foreach { ul =>
      files.map { file =>
        ul.appendChild(liHref(downloadBaseUrl / file)(file).render)
      }
    }
  }
}
