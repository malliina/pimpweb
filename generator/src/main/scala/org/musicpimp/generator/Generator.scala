package org.musicpimp.generator

import java.nio.file.{Files, Path, Paths}

import org.musicpimp.generator.gcp.{FileUtils, GCP}
import play.api.libs.json.JsError

import scala.jdk.CollectionConverters.IteratorHasAsScala

object Generator {
  def mappings(assets: AssetsManifest, to: Path): MappedAssets = {
    def map(files: Seq[Path], folder: String) = files.map { src =>
      val dest = to / "assets" / folder / src.getFileName
      FileMapping(src, "/" + to.relativize(dest).toString.replace('\\', '/'))
    }
    val staticMappings = assets.statics.map { s =>
      FileMapping(s, "/" + assets.assetsBase.relativize(s).toString.replace('\\', '/'))
    }
    MappedAssets(map(assets.scripts, "js"), assets.adhocScripts, map(assets.styles, "css"), staticMappings)
  }

  def main(args: Array[String]): Unit = {
    // TODO use fingerprinted images
    val imgDir = Paths.get("client/src/main/resources/img")
    val imgs = Files.list(imgDir).iterator().asScala.toList.map { img =>
      FileMapping(img, s"img/${img.getFileName.toString}")
    }
    val command = args(0)
    val isLocal = command == "build"
    val routes = if (isLocal) DevRoutes else ProdRoutes
    val assetsJson = Paths.get(args(1))
    val targetDir = Paths.get("target/dist")

    def buildSite() = {
      val manifest = AssetsManifest(assetsJson)
        .fold(err => throw new Exception(s"Failed to read assets file: '${JsError(err)}'."), identity)
      val mapped = mappings(manifest, targetDir)
      Site.complete(mapped.withOther(imgs), routes).write(targetDir)
    }

    // Receives built assets and turns it into a website
    command match {
      case "clean" =>
        FileUtils.deleteDirectory(assetsJson)
      case "build" | "prepare" =>
        buildSite()
      case "deploy" =>
        val built = buildSite()
        val gcp = GCP(BucketName(args(2)))
        val website = Website(routes.index.name, routes.notFound.name, built)
        FileIO.writeJson(website, Paths.get("target/receipt.json"))
        gcp.deploy(website)
      case other =>
        throw new Exception(s"Unknown argument: '$other'.")
    }
  }
}
