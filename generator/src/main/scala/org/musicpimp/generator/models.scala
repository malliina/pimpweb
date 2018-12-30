package org.musicpimp.generator

import java.nio.file.Path

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
  * @param files        files written
  */
case class BuiltSite(files: Seq[Path])
