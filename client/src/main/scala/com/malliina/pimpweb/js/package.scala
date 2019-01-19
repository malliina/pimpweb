package com.malliina.pimpweb

import org.scalajs.dom.raw.XMLHttpRequest
import org.scalajs.dom.{DOMList, Node}
import play.api.libs.json.{JsError, JsValue, Json, Reads}

package object js {

  implicit class NodeListSeq[T <: Node](nodes: DOMList[T]) extends IndexedSeq[T] {
    override def foreach[U](f: T => U): Unit = {
      for (i <- 0 until nodes.length) {
        f(nodes(i))
      }
    }

    override def length: Int = nodes.length

    override def apply(idx: Int): T = nodes(idx)
  }

  implicit class XHRExt(val xhr: XMLHttpRequest) extends AnyVal {
    def json: JsValue = Json.parse(xhr.responseText)

    def validate[T: Reads]: Either[JsError, T] =
      json.validate[T].asEither.left.map(es => JsError(es))
  }

}
