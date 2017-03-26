package com.malliina.pimpweb

import org.scalatest.FunSuite

case class BucketName(name: String)

class S3Tests extends FunSuite {
  ignore("can list files") {
    println(S3FileStore.filenames)
  }
}
