package org.musicpimp.generator.gcp

import java.io._
import java.nio.file._
import java.util.concurrent.Executors
import java.util.zip.GZIPOutputStream

import com.google.cloud.storage.Acl.{Role, User}
import com.google.cloud.storage.{Acl, BlobInfo}
import org.musicpimp.PathUtils
import org.musicpimp.generator.gcp.GCP.executionContext
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters.{asScalaIteratorConverter, mutableSeqAsJavaListConverter}
import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutorService, Future}

object GCP {
  implicit val executionContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  def apply(dist: Path, bucketName: String) = new GCP(dist, bucketName, StorageClient())
}

/** Deploys files in `dist` to `bucketName` in Google Cloud Storage.
  */
class GCP(dist: Path, val bucketName: String, client: StorageClient) {
  private val log = LoggerFactory.getLogger(getClass)
  val bucket = client.bucket(bucketName)

  val defaultContentType = "application/octet-stream"
  val eternalCache = "public, max-age=31536000"
  val contentTypes = Map(
    "html" -> "text/html",
    "json" -> "application/json",
    "js" -> "text/javascript",
    "css" -> "text/css",
    "jpg" -> "image/jpg",
    "png" -> "image/png",
    "gif" -> "image/gif",
    "svg" -> "image/svg+xml",
    "woff" -> "font/woff",
    "woff2" -> "font/woff2",
    "eot" -> "font/eot",
    "ttf" -> "font/ttf",
    "otf" -> "font/otf"
  )

  val defaultCacheControl = "public, max-age=60"
  val cacheControls = Map(
    "js" -> eternalCache,
    "css" -> eternalCache,
    "jpg" -> eternalCache,
    "png" -> eternalCache,
    "svg" -> eternalCache,
    "html" -> "public, max-age=60"
  )
  val htmlExt = ".html"

  def deployDryRun(): Unit = {
    Files.walk(dist).iterator().asScala.toList.filter(p => Files.isRegularFile(p)).foreach { p =>
      val relative = dist.relativize(p).toString.replace('\\', '/')
      println(relative)
    }
  }

  def deploy(indexFile: String, notFoundFile: String, stripHtmlExt: Boolean = true): Unit = {
    def keyFor(relativePath: String) = {
      val chopped =
        if (stripHtmlExt && relativePath.endsWith(htmlExt)) relativePath.dropRight(htmlExt.length)
        else relativePath
      if (chopped.startsWith("/")) chopped.drop(1) else chopped
    }

    val files = Files.walk(dist).iterator().asScala.toList.filter(p => Files.isRegularFile(p))
    val uploads = Future.traverse(files) { file =>
      val relativePath = dist.relativize(file).toString.replace('\\', '/')
      upload(file, keyFor(relativePath))
    }
    Await.result(uploads, 180.seconds)
    val indexKey = keyFor(indexFile)
    bucket.toBuilder.setIndexPage(indexKey).build().update()
    log.info(s"Set index page to '$indexKey'.")
    val notFoundKey = keyFor(notFoundFile)
    bucket.toBuilder.setNotFoundPage(notFoundKey).build().update()
    log.info(s"Set 404 page to '$notFoundKey'.")
    log.info(s"Deployed to '$bucketName'.")
    executionContext.shutdown()
  }

  def upload(file: Path, key: String): Future[Path] = Future {
    val name = file.getFileName.toString
    val extension = PathUtils.ext(file)
    val contentType = contentTypes.getOrElse(extension, defaultContentType)
    val isFingerprinted = name.count(_ == '.') > 1
    val cacheControl =
      if (key.startsWith("assets/static")) eternalCache
      else if (isFingerprinted) cacheControls.getOrElse(extension, defaultCacheControl)
      else defaultCacheControl
    val blob = BlobInfo.newBuilder(bucketName, key)
      .setContentType(contentType)
      .setAcl(mutable.Buffer(Acl.of(User.ofAllUsers(), Role.READER)).asJava)
      .setContentEncoding("gzip")
      .setCacheControl(cacheControl)
      .build()
    val gzipFile = Files.createTempFile(name, "gz")
    gzip(file, gzipFile)
    client.upload(blob, gzipFile)
    log.info(s"Uploaded '$key' as '$contentType' with cache '$cacheControl' to '$bucketName' from '$file'.")
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
