package com.malliina.generator.gcp

import java.nio.file._
import java.util.concurrent.Executors

import com.google.cloud.storage.Acl.{Role, User}
import com.google.cloud.storage.{Acl, BlobInfo}
import com.malliina.generator.gcp.GCP.executionContext
import com.malliina.generator.{BucketName, BuiltSite, ContentTypes, FileIO, WebsiteFile}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutorService, Future}
import scala.jdk.CollectionConverters.{IteratorHasAsScala, SeqHasAsJava}

object GCP {
  implicit val executionContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  def apply(bucketName: BucketName) = new GCP(bucketName, StorageClient())

  def close(): Unit = executionContext.shutdown()
}

/** Deploys files in `dist` to `bucketName` in Google Cloud Storage.
  */
class GCP(val bucketName: BucketName, client: StorageClient) {
  private val log = LoggerFactory.getLogger(getClass)
  val bucket = client.bucket(bucketName)

  val contentTypes = ContentTypes.contentTypes

  def deployDryRun(dist: Path): Unit = {
    Files.walk(dist).iterator().asScala.toList.filter(p => Files.isRegularFile(p)).foreach { p =>
      val relative = dist.relativize(p).toString.replace('\\', '/')
      println(relative)
    }
  }

  def deploy(website: BuiltSite): Unit = {
    log.info(s"Deploying to GCP bucket '$bucketName'...")
    val uploads = Future.traverse(website.files) { file =>
      upload(file)
    }
    Await.result(uploads, 180.seconds)
    bucket.toBuilder.setIndexPage(website.index.value).build().update()
    log.info(s"Set index page to '${website.index}'.")
    bucket.toBuilder.setNotFoundPage(website.notFound.value).build().update()
    log.info(s"Set 404 page to '${website.notFound}'.")
    log.info(s"Deployed to GCP bucket '$bucketName'.")
    executionContext.shutdown()
  }

  private def upload(websiteFile: WebsiteFile): Future[Path] = Future {
    val key = websiteFile.key
    val file = websiteFile.file
    val name = websiteFile.name
    val contentType = websiteFile.contentType
    val blob = BlobInfo
      .newBuilder(bucketName.value, key.value)
      .setContentType(contentType.value)
      .setAcl(Seq(Acl.of(User.ofAllUsers(), Role.READER)).asJava)
      .setContentEncoding("gzip")
      .setCacheControl(websiteFile.cacheControl.value)
      .build()
    val gzipFile = Files.createTempFile(name, "gz")
    FileIO.gzip(file, gzipFile)
    client.upload(blob, gzipFile)
    log.info(s"Uploaded '$file' as '$key' of '$contentType' with cache '${websiteFile.cacheControl}' to '$bucketName'.")
    gzipFile
  }
}
