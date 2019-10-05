package org.musicpimp.generator

import java.nio.file.{Files, Path, Paths}

import org.musicpimp.generator.gcp.{FileUtils, GCP}
import play.api.libs.json.JsError

import scala.jdk.CollectionConverters.IteratorHasAsScala

object Generator {
  def mappings(assets: AssetsManifest, to: Path): MappedAssets = {
    def map(files: Seq[Path], folder: String) = files.map { src =>
      val dest = to / "assets" / folder / src.getFileName
      FileMapping(src, "/" + to.relativize(dest).toString.replace('\\', '/'), isFingerprinted = true)
    }
    val staticMappings = assets.statics.map { s =>
      FileMapping(s, "/" + assets.assetsBase.relativize(s).toString.replace('\\', '/'), isFingerprinted = true)
    }
    MappedAssets(map(assets.scripts, "js"), assets.adhocScripts, map(assets.styles, "css"), staticMappings)
  }

  def main(args: Array[String]): Unit = {
    val digests = Digests
    val assetFinder = new DigestFinder(digests)
    val base = Paths.get("client/src/main/resources")
    val imgDir = base.resolve("img")
    val imgs = Files.list(imgDir).iterator().asScala.toList.map { img =>
      val digested = digests.compute(img)
      val undigested = AssetPath(s"/img/${img.getFileName}")
      FileMapping(img, assetFinder.digestedPath(undigested, digested.hash), isFingerprinted = true)
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
      Site.complete(mapped.withOther(imgs), routes, assetFinder).write(targetDir)
    }

    // Receives built assets and turns it into a website
    command match {
      case "clean" =>
        FileUtils.deleteDirectory(assetsJson)
      case "build" | "prepare" =>
        val built = buildSite()
        FileIO.writeJson(built, Paths.get("target/built.json"))
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
