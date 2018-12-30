package com.malliina.pimpweb.js

import play.api.libs.json.Json

case class StorageItem(name: String, bucket: String)

object StorageItem {
  implicit val json = Json.format[StorageItem]
}

case class ListObjectsResponse(items: Seq[StorageItem])

object ListObjectsResponse {
  implicit val json = Json.format[ListObjectsResponse]
}
