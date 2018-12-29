import java.nio.file.Path

/**
  * @param from local file
  * @param to   relative to the target directory
  */
case class FileMapping(from: Path, to: String)

case class SiteSpec(css: Seq[String], js: Seq[String], assets: Seq[FileMapping], targetDirectory: Path)

/**
  * @param indexPage    name of index.html, typically "index.html"
  * @param notFoundPage name of 404 error page
  * @param files        files written
  */
case class BuiltSite(indexPage: String, notFoundPage: String, files: Seq[Path])

object BuiltSite {
  val indexHtml = "index.html"
}
