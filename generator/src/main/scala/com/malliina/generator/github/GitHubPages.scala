package com.malliina.generator.github

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import com.malliina.generator.{BuiltSite, CompleteSite, ExitValue, FileIO, IO}

object GitHubPages {
  def apply(cname: String): GitHubPages = new GitHubPages(cname)
}

class GitHubPages(cname: String) {
  def build(site: CompleteSite, to: Path): BuiltSite = {
    val content = site.write(to)
    FileIO.write(cname.getBytes(StandardCharsets.UTF_8), to.resolve("CNAME"))
    content
  }

  /** The dirty flag is because the build copies also non-documentation files to the site directory,
    * after which we reuse the documentation deployment functionality to deploy the whole site.
    */
  def deploy(site: BuiltSite): ExitValue = IO.run("mkdocs gh-deploy --dirty")
}
