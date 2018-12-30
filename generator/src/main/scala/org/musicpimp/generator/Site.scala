package org.musicpimp.generator

import java.nio.file.{Files, Path, StandardCopyOption, StandardOpenOption}

import org.slf4j.LoggerFactory

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
      html.index -> routes.index,
      html.notFound -> routes.notFound,
      html.about -> routes.about,
      html.forum -> routes.forum,
      html.docWin -> routes.docs,
      html.docDeb -> routes.docsDeb,
      html.docRpm -> routes.docsRpm,
      html.docMac -> routes.docsMac,
      html.docWinPhone -> routes.wp,
      html.alarms -> routes.docsAlarms,
      html.privacyPolicy -> routes.legalPrivacy,
      html.downloads(Home.releaseDate, Nil) -> routes.downloads
    ).mapValues { v => v.file }

    BuiltSite(
      fileMap.map { case (page, dest) => write(page, target.resolve(dest)) }.toList
    )
  }

  def write(page: TagPage, to: Path): Path = {
    if (!Files.isRegularFile(to)) {
      val dir = to.getParent
      if (!Files.isDirectory(dir))
        Files.createDirectories(dir)
      Files.createFile(to)
    }
    Files.write(to, page.toBytes, StandardOpenOption.TRUNCATE_EXISTING)
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
