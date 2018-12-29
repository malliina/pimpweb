import java.nio.file.{Files, Path, StandardCopyOption, StandardOpenOption}

import sbt.Logger

object Site {
  /** Builds HTML for the static site.
    *
    * @param spec site options
    * @return files written
    */
  def build(spec: SiteSpec, log: Logger): BuiltSite = {
    val target = spec.targetDirectory
    val html = PimpWebHtml(spec.css, spec.js)
    val notFoundFile = "notfound.html"
    spec.assets.map { asset =>
      copy(asset.from, target.resolve(asset.to))
    }

    def writePage(page: TagPage, to: String) = write(page, target.resolve(to), log)

    BuiltSite(
      BuiltSite.indexHtml,
      notFoundFile,
      Seq(
        writePage(html.index, BuiltSite.indexHtml),
        writePage(html.notFound, notFoundFile),
        writePage(html.about, "about.html"),
        writePage(html.forum, "forum.html"),
        writePage(html.docWin, "docs.html"),
        writePage(html.downloads(Home.releaseDate, Nil), "downloads.html"),
      )
    )
  }

  def write(page: TagPage, to: Path, log: Logger): Path = {
    if (!Files.exists(to)) {
      val dir = to.getParent
      if (!Files.exists(dir))
        Files.createDirectories(dir)
      Files.createFile(to)
    }
    Files.write(to, page.toBytes, StandardOpenOption.TRUNCATE_EXISTING)
    log.info(s"Wrote $to.")
    to
  }

  def copy(from: Path, to: Path) = {
    val dir = to.getParent
    if (!Files.exists(dir))
      Files.createDirectories(dir)
    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
  }
}
