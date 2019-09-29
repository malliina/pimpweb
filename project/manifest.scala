import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import play.api.libs.json._

import Formats.pathFormat

object Formats {
  implicit val pathFormat: Format[Path] = Format[Path](
    Reads(json => json.validate[String].map(s => Paths.get(s))),
    Writes(p => Json.toJson(p.toAbsolutePath.toString))
  )
}

case class FileMapping(from: Path, to: String)

object FileMapping {
  implicit val json = Json.format[FileMapping]
}

case class SiteManifest(css: Seq[String],
                        js: Seq[String],
                        assets: Seq[FileMapping],
                        statics: Seq[String],
                        targetDirectory: Path) {
  def to(file: Path) =
    Files.write(file, Json.toBytes(SiteManifest.json.writes(this)), StandardOpenOption.CREATE)
}

object SiteManifest {
  implicit val json = Json.format[SiteManifest]

  def apply(file: Path): JsResult[SiteManifest] =
    Json.parse(Files.readAllBytes(file)).validate[SiteManifest]
}
