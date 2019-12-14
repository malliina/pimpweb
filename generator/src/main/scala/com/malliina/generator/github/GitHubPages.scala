package com.malliina.generator.github

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import com.malliina.generator.{BuiltSite, CompleteSite, FileIO}

object GitHubPages {
  def apply(cname: String): GitHubPages = new GitHubPages(cname)
}

class GitHubPages(cname: String) {
  def build(site: CompleteSite, to: Path): BuiltSite = {
    val content = site.write(to)
    FileIO.write(cname.getBytes(StandardCharsets.UTF_8), to.resolve("CNAME"))
    content
  }
}
