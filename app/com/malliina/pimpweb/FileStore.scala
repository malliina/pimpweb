package com.malliina.pimpweb

import java.net.URI

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.malliina.azure.{AzureStorageCredentialReader, StorageClient}

trait FileStore {
  def filenames: Seq[String]
}

object S3FileStore extends FileStore {

  import collection.JavaConversions._

  val bucketName = "files.musicpimp.org"
  val aws = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build()

  override def filenames: Seq[String] = {
    val objs = aws.listObjects(bucketName)
    objs.getObjectSummaries.map(_.getKey)
      .filter(_.startsWith("musicpimp"))
      .toList
  }
}

object AzureFileStore extends FileStore {
  override def filenames: Seq[String] = {
    val maybeFiles = AzureStorageCredentialReader.loadOpt.map { creds =>
      val client = new StorageClient(creds.accountName, creds.accountKey)
      val uriStrings = client uris "files"
      uriStrings map fileName filter (_.startsWith("musicpimp"))
    }
    maybeFiles.map(_.toList) getOrElse Nil
  }

  private def fileName(uri: URI) = {
    val uriString = uri.toString
    uriString.substring((uriString lastIndexOf '/') + 1, uriString.length)
  }
}
