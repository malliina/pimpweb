package org.musicpimp.generator

import java.nio.file.Path

import play.api.libs.json.Json

/**
  * @param from local file
  * @param to   relative to the target directory
  */
case class FileMapping(from: Path, to: String)

case class SiteSpec(css: Seq[String],
                    js: Seq[String],
                    assets: Seq[FileMapping],
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
