package org.musicpimp.generator

import java.nio.file.{Files, Paths}

import org.musicpimp.generator.gcp.{FileUtils, GCP}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object Generator {
  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val imgDir = Paths.get("client/img")
    val imgs = Files.list(imgDir).iterator().asScala.toList.map { img =>
      FileMapping(img, s"img/${img.getFileName.toString}")
    }
    val command = args(0)
    val isLocal = command == "build"
    val routes = if (isLocal) DevRoutes else ProdRoutes
    val target = Paths.get(args(1))
    val files = args.drop(2)
    val spec = SiteSpec(
      css = files.filter(_.endsWith(".css")),
      js = files.filter(_.endsWith(".js")),
      assets = imgs,
      statics = files.filter(f => f.startsWith("/assets") && !f.endsWith(".css") && !f.endsWith(".js")),
      targetDirectory = target,
      routes
    )
    command match {
      case "clean" =>
        FileUtils.deleteDirectory(target)
      case "build" =>
        Site.build(spec)
      case "prepare" =>
        Site.build(spec)
      case "deploy" =>
        val gcp = GCP(target, bucketName = args(2))
        log.info(s"Deploying ${spec.targetDirectory} to ${gcp.bucketName}...")
        gcp.deploy(routes.index.name, routes.notFound.name)
      case other =>
        throw new Exception(s"Unknown argument: '$other'.")
    }
  }
}
