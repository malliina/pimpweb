package com.malliina.generator

import java.nio.file.{Files, Path, Paths}

import com.malliina.generator.gcp.GCP
import com.malliina.generator.netlify.Netlify
import play.api.libs.json.{JsError, Json}

import scala.jdk.CollectionConverters.IteratorHasAsScala

trait Generator {
  val target = Paths.get("target")
  val distDir = Paths.get("site")
  val docsDir = Paths.get("docs")
  val resourcesBaseDir = Paths.get("client/src/main/resources")

  /** Generates the HTML of the site.
    *
    * @param assets generated assets
    * @param assetFinder source of assets - possibly fingerprinted
    * @param mode dev or prod
    * @return page and path mappings
    */
  def pages(assets: MappedAssets, assetFinder: AssetFinder, mode: AppMode): BuiltPages

  def main(args: Array[String]): Unit =
    generate(BuildCommand(args(0), Paths.get(args(1)), if (args.length > 2) Option(BucketName(args(2))) else None))

  def render(assets: MappedAssets, assetFinder: AssetFinder, mode: AppMode): CompleteSite = {
    val buildInfo = Seq(ByteMapping(Json.toBytes(Json.toJson(VersionInfo.default)), "build.json"))
    val ps = pages(assets, assetFinder, mode)
    assets.site(ps.pages, buildInfo, ps.index, ps.notFound)
  }

  def generate(cmd: BuildCommand) = {
    val digests = Digests
    val assetFinder = new DigestFinder(digests)
    // Fingerprints images
    val imgDir = resourcesBaseDir.resolve("img")
    val imgs = Files.list(imgDir).iterator().asScala.toList.map { img =>
      val digested = digests.compute(img)
      val undigested = AssetPath(s"/img/${img.getFileName}")
      FileMapping(img, assetFinder.digestedPath(undigested, digested.hash), isFingerprinted = true)
    }
    val mode = if (cmd.cmd == "build") AppMode.Dev else AppMode.Prod
    val assetsJson = cmd.manifest

    def buildSite(dist: Path): BuiltSite = {
      val manifest = AssetsManifest(assetsJson)
        .fold(err => fail(s"Failed to read assets file: '${JsError(err)}'."), identity)
      val mapped = mappings(manifest, dist)
      render(mapped.withOther(imgs), assetFinder, mode).write(dist)
    }

    // Receives built assets and turns it into a website
    cmd.cmd match {
      case "clean" =>
        FileIO.deleteDirectory(assetsJson)
      case "build" | "prepare" =>
        val built = buildSite(distDir)
        FileIO.writeJson(built, target.resolve("built.json"))
      case "gh" =>
        buildSite(docsDir)
      case "netlify" =>
        val site = buildSite(distDir)
        Netlify.headers(site.files, distDir.resolve("_headers"))
      case "deploy" =>
        cmd.bucket
          .map { bucket =>
            val website: BuiltSite = buildSite(distDir)
            val gcp = GCP(bucket)
            FileIO.writeJson(website, target.resolve("receipt.json"))
            gcp.deploy(website)
          }
          .getOrElse {
            fail("No bucket name defined.")
          }
      case other =>
        fail(s"Unknown argument: '$other'.")
    }
  }

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

  def fail(message: String) = throw new Exception(message)
}
