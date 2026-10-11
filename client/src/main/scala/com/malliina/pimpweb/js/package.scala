package com.malliina.pimpweb

import io.circe.Decoder
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.{DOMList, Node}

package object js:

  implicit class NodeListSeq[T <: Node](nodes: DOMList[T]) extends IndexedSeq[T]:
    override def foreach[U](f: T => U): Unit =
      for i <- 0 until nodes.length do f(nodes(i))

    override def length: Int = nodes.length

    override def apply(idx: Int): T = nodes(idx)

  implicit class XHRExt(val xhr: XMLHttpRequest) extends AnyVal:
    def json = io.circe.parser.parse(xhr.responseText)

    def validate[T: Decoder] =
      json.flatMap(parsed => parsed.as[T])
