package org.musicpimp.generator

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import org.musicpimp.generator.gcp.{FileUtils, GCP}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json

import scala.jdk.CollectionConverters.IteratorHasAsScala

object Generator {
  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val imgDir = Paths.get("client/src/main/resources/img")

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
      case "manifest" =>
        SiteManifest(target)
      case "clean" =>
        FileUtils.deleteDirectory(target)
      case "build" =>
        Site.build(spec)
      case "prepare" =>
        Site.build(spec)
      case "deploy" =>
        val gcp = GCP(target, bucketName = BucketName(args(2)))
        log.info(s"Deploying ${spec.targetDirectory} to ${gcp.bucketName}...")
        gcp.deploy(Website(routes.index.name, routes.notFound.name, WebsiteFile.list(target, CacheControls)))
      case "website" =>
        val web = Website(routes.index.name, routes.notFound.name, WebsiteFile.list(target, CacheControls))
        val out = Paths.get("website.json")
        Files.write(out, Json.prettyPrint(Json.toJson(web)).getBytes(StandardCharsets.UTF_8))
        log.info(s"Wrote '${out.toAbsolutePath}'.")
      case other =>
        throw new Exception(s"Unknown argument: '$other'.")
    }
  }
}
