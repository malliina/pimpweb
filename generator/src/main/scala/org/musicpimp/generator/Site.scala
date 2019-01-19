package org.musicpimp.generator

import java.nio.file.{Files, Path, StandardCopyOption, StandardOpenOption}

import org.slf4j.LoggerFactory
import play.api.libs.json.Json

object Site {
  private val log = LoggerFactory.getLogger(getClass)

  /** Builds HTML for the static site.
    *
    * @param spec site options
    * @return files written
    */
  def build(spec: SiteSpec): BuiltSite = {
    val target = spec.targetDirectory
    val routes = spec.routes
    val html = PimpWebHtml(spec.css, spec.js, spec.routes)
    spec.assets.foreach { asset =>
      copy(asset.from, target.resolve(asset.to))
    }

    val fileMap = Map(
      routes.index -> html.index,
      routes.notFound -> html.notFound,
      routes.about -> html.about,
      routes.forum -> html.forum,
      routes.docs -> html.docWin,
      routes.docsDeb -> html.docDeb,
      routes.docsRpm -> html.docRpm,
      routes.docsMac -> html.docMac,
      routes.wp -> html.docWinPhone,
      routes.docsAlarms -> html.alarms,
      routes.legalPrivacy -> html.privacyPolicy,
      routes.downloads -> html.downloads(Home.releaseDate, Nil),
      routes.ping -> html.ping,
    )
    BuiltSite(
      fileMap.map { case (route, page) => write(page, target.resolve(route.file)) }.toList ++
        List(write(Json.toBytes(Json.toJson(VersionInfo.default)), target.resolve(routes.build.file)))
    )
  }

  def write(page: TagPage, to: Path): Path = write(page.toBytes, to)

  def write(bytes: Array[Byte], to: Path): Path = {
    if (!Files.isRegularFile(to)) {
      val dir = to.getParent
      if (!Files.isDirectory(dir))
        Files.createDirectories(dir)
      Files.createFile(to)
    }
    Files.write(to, bytes, StandardOpenOption.TRUNCATE_EXISTING)
    log.info(s"Wrote ${to.toAbsolutePath}.")
    to
  }

  def copy(from: Path, to: Path): Unit = {
    val dir = to.getParent
    if (!Files.isDirectory(dir))
      Files.createDirectories(dir)
    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
    log.info(s"Copied $from to $to.")
  }
}
