package org.musicpimp.generator

import java.nio.file.{Files, Paths}

import org.musicpimp.generator.gcp.GCP
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object Generator {
  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    log.info(s"${args.toList}")
    val imgDir = Paths.get("client/img")
    val imgs = Files.list(imgDir).iterator().asScala.toList.map { img =>
      FileMapping(img, s"img/${img.getFileName.toString}")
    }
    val target = Paths.get(args(1))
    val spec = SiteSpec(
      css = args.filter(_.endsWith(".css")),
      js = args.filter(_.endsWith(".js")),
      assets = imgs,
      targetDirectory = target,
      SiteRoutes
    )
    args(0) match {
      case "build" =>
        Site.build(spec)
      case "deploy" =>
        val gcp = GCP(target)
        log.info(s"Deploying ${spec.targetDirectory} to ${gcp.bucketName}...")
        gcp.deploy(spec.routes.index.name, spec.routes.notFound.name)
//        gcp.deployDryRun()
      case other =>
        throw new Exception(s"Unknown argument: '$other'.")
    }
  }
}
