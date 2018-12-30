package org.musicpimp.generator.gcp

import java.io._
import java.nio.file._
import java.util.zip.GZIPOutputStream

import com.google.cloud.storage.Acl.{Role, User}
import com.google.cloud.storage.{Acl, BlobInfo}
import org.musicpimp.PathUtils
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters.{asScalaIteratorConverter, mutableSeqAsJavaListConverter}
import scala.collection.mutable

object GCP {
  def apply(dist: Path) = new GCP(dist, "www.musicpimp.org", StorageClient())
}

/** Deploys files in `dist` to `bucketName` in Google Cloud Storage.
  */
class GCP(dist: Path, val bucketName: String, client: StorageClient) {
  private val log = LoggerFactory.getLogger(getClass)
  val bucket = client.bucket(bucketName)

  val defaultContentType = "application/octet-stream"
  val contentTypes = Map(
    "html" -> "text/html",
    "js" -> "text/javascript",
    "css" -> "text/css",
    "jpg" -> "image/jpg",
    "png" -> "image/png",
    "gif" -> "image/gif",
    "svg" -> "image/svg+xml"
  )

  val defaultCacheControl = "private, max-age=0"
  val cacheControls = Map(
    "js" -> "public, max-age=31536000",
    "css" -> "public, max-age=31536000",
    "jpg" -> "public, max-age=31536000",
    "png" -> "public, max-age=31536000",
    "svg" -> "public, max-age=31536000",
    "html" -> "public, max-age=10"
  )

  def deployDryRun(): Unit = {
    Files.walk(dist).iterator().asScala.toList.filter(p => Files.isRegularFile(p)).foreach { p =>
      val relative = dist.relativize(p).toString.replace('\\', '/')
      println(relative)
    }
  }

  def deploy(indexFile: String, notFoundFile: String, stripHtmlExt: Boolean = true): Unit = {
    def keyFor(relativePath: String) = {
      val chopped =
        if (stripHtmlExt && relativePath.endsWith(".html")) relativePath.dropRight(".html".length)
        else relativePath
      if (chopped.startsWith("/")) chopped.drop(1) else chopped
    }

    val files = Files.walk(dist).iterator().asScala.toList.filter(p => Files.isRegularFile(p))
    files.foreach { file =>
      val name = file.getFileName.toString
      val extension = PathUtils.ext(file)
      val contentType = contentTypes.getOrElse(extension, defaultContentType)
      val isFingerprinted = name.count(_ == '.') > 1
      val cacheControl =
        if (isFingerprinted) cacheControls.getOrElse(extension, defaultCacheControl)
        else defaultCacheControl
      val relativePath = dist.relativize(file).toString.replace('\\', '/')
      val blob = BlobInfo.newBuilder(bucketName, keyFor(relativePath))
        .setContentType(contentType)
        .setAcl(mutable.Buffer(Acl.of(User.ofAllUsers(), Role.READER)).asJava)
        .setContentEncoding("gzip")
        .setCacheControl(cacheControl)
        .build()
      val gzipFile = Files.createTempFile(name, "gz")
      gzip(file, gzipFile)
      client.upload(blob, gzipFile)
      log.info(s"Uploaded '$file' to '$bucketName' as '$contentType'.")
    }
    val indexKey = keyFor(indexFile)
    bucket.toBuilder.setIndexPage(indexKey).build().update()
    log.info(s"Set index page to '$indexKey'.")
    val notFoundKey = keyFor(notFoundFile)
    bucket.toBuilder.setNotFoundPage(notFoundKey).build().update()
    log.info(s"Set 404 page to '$notFoundKey'.")
    log.info(s"Deployed to '$bucketName'.")
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

  def using[T <: AutoCloseable, U](res: T)(code: T => U): U = try {
    code(res)
  } finally {
    res.close()
  }

  def ext(path: Path) = {
    val name = path.getFileName.toString
    val idx = name.lastIndexOf('.')
    if (idx >= 0 && name.length > idx + 1) name.substring(idx + 1)
    else ""
  }
}
