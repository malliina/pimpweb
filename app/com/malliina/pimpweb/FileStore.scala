package com.malliina.pimpweb

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder

import scala.collection.JavaConverters.asScalaBuffer

trait FileStore {
  def filenames: Seq[String]
}

object S3FileStore extends FileStore {
  val bucketName = "files.musicpimp.org"

  val builder = AmazonS3ClientBuilder.standard().withCredentials(
    new AWSCredentialsProviderChain(
      new ProfileCredentialsProvider("pimp"),
      DefaultAWSCredentialsProviderChain.getInstance()
    )
  )
  val aws = builder.withRegion(Regions.EU_WEST_1).build()

  override def filenames: Seq[String] = {
    val objs = aws.listObjects(bucketName)
    asScalaBuffer(objs.getObjectSummaries)
      .map(_.getKey)
      .filter(_.startsWith("musicpimp"))
      .toList
  }
}
