package org.musicpimp.generator

import java.nio.file.{Files, Path, StandardOpenOption}

import play.api.libs.json.{JsResult, Json}

/**
  * @param from local file
  * @param to   relative to the target directory
  */
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

case class SiteSpec(css: Seq[String],
                    js: Seq[String],
                    assets: Seq[FileMapping],
                    statics: Seq[String],
                    targetDirectory: Path,
                    routes: Routes)

/**
  * @param files files written
  */
case class BuiltSite(files: Seq[Path])

case class VersionInfo(name: String, version: String, gitHash: String)

object VersionInfo {
  implicit val json = Json.format[VersionInfo]

  val default: VersionInfo = VersionInfo(BuildInfo.name, BuildInfo.version, BuildInfo.gitHash)
}
