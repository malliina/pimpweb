package com.malliina.generator

import java.nio.file.{Files, Path, Paths}
import com.malliina.generator.Command.{Build, Deploy}
import com.malliina.generator.DeployTarget.{GCPTarget, GitHubTarget, NetlifyTarget}
import com.malliina.generator.gcp.GCPClient
import com.malliina.generator.github.GitHubPages
import com.malliina.generator.netlify.NetlifyClient
import org.musicpimp.generator.PrivacyPolicy
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

  def parseCommand(args: Seq[String]): BuildSpec = {
    val path = Paths.get(args.head)
    Json.parse(Files.readAllBytes(path)).as[BuildSpec]
  }

  def main(args: Array[String]): Unit = generate(parseCommand(args))

  def render(assets: MappedAssets, assetFinder: AssetFinder, mode: AppMode): CompleteSite = {
    val jsonFiles = Seq(
      ByteMapping(Json.toBytes(Json.toJson(VersionInfo.default)), "build.json"),
      ByteMapping(Json.toBytes(PrivacyPolicy.json), "legal/privacy.json")
    )
    val ps = pages(assets, assetFinder, mode)
    assets.site(ps.pages, jsonFiles, ps.index, ps.notFound)
  }

  def generate(spec: BuildSpec) = {
    val digests = Digests
    val assetFinder = new DigestFinder(digests)
    // Fingerprints images
    val mode = if (spec.cmd == Build) AppMode.Dev else AppMode.Prod
    val imgDir = resourcesBaseDir.resolve("img")
    val imgs = Files.list(imgDir).iterator().asScala.toList.map { img =>
      val digested = digests.compute(img)
      val undigested = AssetPath(s"/img/${img.getFileName}")
      FileMapping(img, assetFinder.digestedPath(undigested, digested.hash), isFingerprinted = true)
    }
    val assetsJson = spec.manifest

    def compileSite(dist: Path): CompleteSite = {
      val manifest = AssetsManifest(assetsJson)
        .fold(err => fail(s"Failed to read assets file: '${JsError(err)}'."), identity)
      val mapped = mappings(manifest, dist, mode)
      // Writes files to dist
      render(mapped.withOther(imgs), assetFinder, mode)
    }

    def buildSite(dist: Path): BuiltSite = compileSite(dist).write(dist)

    spec.target match {
      case NetlifyTarget =>
        spec.cmd match {
          case Build =>
            NetlifyClient.build(compileSite(distDir), distDir)
          case Deploy =>
            val built = NetlifyClient.build(compileSite(distDir), distDir)
            NetlifyClient.deploy(built)
        }
      case GCPTarget(bucket) =>
        spec.cmd match {
          case Build =>
            buildSite(distDir)
          case Deploy =>
            val website: BuiltSite = buildSite(distDir)
            val gcp = GCPClient(bucket)
            FileIO.writeJson(website, target.resolve("receipt.json"))
            gcp.deploy(website)
        }
      case GitHubTarget(cname) =>
        spec.cmd match {
          case Build =>
            GitHubPages(cname).build(compileSite(distDir), distDir)
          case Deploy =>
            val gh = GitHubPages(cname)
            val website: BuiltSite = gh.build(compileSite(distDir), distDir)
            gh.deploy(website)
        }
    }
  }

  def mappings(assets: AssetsManifest, to: Path, mode: AppMode): MappedAssets = {
    val prefix = if (mode == AppMode.Dev) "" else "/"
    def map(files: Seq[Path], folder: String) = files.map { src =>
      val dest = to / "assets" / folder / src.getFileName
      FileMapping(src, prefix + to.relativize(dest).toString.replace('\\', '/'), isFingerprinted = true)
    }
    val staticMappings = assets.statics.map { s =>
      FileMapping(s, prefix + assets.assetsBase.relativize(s).toString.replace('\\', '/'), isFingerprinted = true)
    }
    MappedAssets(map(assets.scripts, "js"), assets.adhocScripts, map(assets.styles, "css"), staticMappings)
  }

  def fail(message: String) = throw new Exception(message)
}
