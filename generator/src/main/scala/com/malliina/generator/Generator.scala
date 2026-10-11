package com.malliina.generator

import com.malliina.assets.FileAssets

import java.nio.file.{Files, Path, Paths}
import com.malliina.generator.Command.{Build, Deploy}
import com.malliina.generator.netlify.NetlifyClient
import io.circe.Encoder
import org.musicpimp.generator.PrivacyPolicy
import io.circe.syntax.EncoderOps

import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters.IteratorHasAsScala

trait Generator:

  val target = Paths.get("target")
  val distDir = BuildInfo.siteDir.toPath
  val docsDir = Paths.get("docs")
  val resourcesBaseDir = Paths.get("client/src/main/resources")

  val digests = Digests
  val assetFinder = new DigestFinder(digests)
  val mode = if BuildInfo.isProd then AppMode.Prod else AppMode.Dev

  /** Generates the HTML of the site.
    *
    * @param assets
    *   generated assets
    * @param assetFinder
    *   source of assets - possibly fingerprinted
    * @param mode
    *   dev or prod
    * @return
    *   page and path mappings
    */
  def pages(assets: MappedAssets, assetFinder: AssetFinder, mode: AppMode): BuiltPages

  def main(args: Array[String]): Unit =
    generate()

  def render(assets: MappedAssets, assetFinder: AssetFinder, mode: AppMode): CompleteSite =
    val jsonFiles = Seq(
      ByteMapping(toBytes(VersionInfo.default), "build.json"),
      ByteMapping(toBytes(PrivacyPolicy.json), "legal/privacy.json")
    )
    val ps = pages(assets, assetFinder, mode)
    assets.site(ps.pages, jsonFiles, ps.index, ps.notFound)

  def toBytes[T: Encoder](t: T) = t.asJson.spaces2.getBytes(StandardCharsets.UTF_8)

  def generate() =
    // Fingerprints images
    val imgDir = resourcesBaseDir.resolve("img")
    val imgs = Files
      .list(imgDir)
      .iterator()
      .asScala
      .toList
      .map: img =>
        val digested = digests.compute(img)
        val undigested = AssetPath(s"/img/${img.getFileName}")
        FileMapping(
          img,
          assetFinder.digestedPath(undigested, digested.hash),
          isFingerprinted = true
        )

    val manifest = AssetsManifest(Nil, Nil, Nil, Nil, BuildInfo.siteDir.toPath)
    val mapped = mappings(manifest, distDir, mode)
    render(mapped.withOther(imgs), assetFinder, mode)
    val complete = render(mapped.withOther(imgs), assetFinder, mode)

    NetlifyClient.build(complete, distDir)

  private def mappings(assets: AssetsManifest, to: Path, mode: AppMode): MappedAssets =
    val prefix = if mode == AppMode.Dev then "" else "/"
    def map(files: Seq[Path], folder: String) = files.map: src =>
      val dest = to / "assets" / folder / src.getFileName
      FileMapping(
        src,
        prefix + to.relativize(dest).toString.replace('\\', '/'),
        isFingerprinted = true
      )
    val staticMappings = assets.statics.map: s =>
      FileMapping(
        s,
        prefix + assets.assetsBase.relativize(s).toString.replace('\\', '/'),
        isFingerprinted = true
      )
    MappedAssets(
      map(assets.scripts, "js"),
      assets.adhocScripts,
      map(assets.styles, "css"),
      staticMappings
    )
