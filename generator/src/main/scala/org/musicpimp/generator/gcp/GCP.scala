package org.musicpimp.generator.gcp

import java.io._
import java.nio.file._
import java.util.concurrent.Executors
import java.util.zip.GZIPOutputStream

import com.google.cloud.storage.Acl.{Role, User}
import com.google.cloud.storage.{Acl, BlobInfo}
import org.musicpimp.generator.gcp.GCP.executionContext
import org.musicpimp.generator.{BucketName, ContentTypes, Website, WebsiteFile}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutorService, Future}
import scala.jdk.CollectionConverters.{IteratorHasAsScala, SeqHasAsJava}

object GCP {
  implicit val executionContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  def apply(dist: Path, bucketName: BucketName) = new GCP(bucketName, StorageClient())
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

  def deploy(website: Website): Unit = {
    val uploads = Future.traverse(website.files) { file =>
      upload(file)
    }
    Await.result(uploads, 180.seconds)
    bucket.toBuilder.setIndexPage(website.indexKey.value).build().update()
    log.info(s"Set index page to '${website.indexKey}'.")
    bucket.toBuilder.setNotFoundPage(website.notFoundKey.value).build().update()
    log.info(s"Set 404 page to '${website.notFoundKey}'.")
    log.info(s"Deployed to '$bucketName'.")
    executionContext.shutdown()
  }

  def upload(websiteFile: WebsiteFile): Future[Path] = Future {
    val key = websiteFile.key
    val file = websiteFile.file
    val name = websiteFile.name
    val contentType = ContentTypes.resolve(file)
    val blob = BlobInfo
      .newBuilder(bucketName.name, key.value)
      .setContentType(contentType.value)
      .setAcl(Seq(Acl.of(User.ofAllUsers(), Role.READER)).asJava)
      .setContentEncoding("gzip")
      .setCacheControl(websiteFile.cacheControl.value)
      .build()
    val gzipFile = Files.createTempFile(name, "gz")
    gzip(file, gzipFile)
    client.upload(blob, gzipFile)
    log.info(s"Uploaded '$file' as '$key' of '$contentType' with cache '${websiteFile.cacheControl}' to '$bucketName'.")
    gzipFile
  }

  def gzip(src: Path, dest: Path): Unit =
    using(new FileInputStream(src.toFile)) { in =>
      using(new FileOutputStream(dest.toFile)) { out =>
        using(new GZIPOutputStream(out, 8192)) { gzip =>
          copyStream(in, gzip)
          gzip.finish()
        }
      }
    }

  // Adapted from sbt-io
  private def copyStream(in: InputStream, out: OutputStream): Unit = {
    val buffer = new Array[Byte](8192)

    def read(): Unit = {
      val byteCount = in.read(buffer)
      if (byteCount >= 0) {
        out.write(buffer, 0, byteCount)
        read()
      }
    }

    read()
  }

  def using[T <: AutoCloseable, U](res: T)(code: T => U): U =
    try {
      code(res)
    } finally {
      res.close()
    }
}
