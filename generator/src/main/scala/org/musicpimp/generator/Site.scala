package org.musicpimp.generator

import play.api.libs.json.Json

object Site {
  def complete(assets: MappedAssets, routes: Routes, assetFinder: AssetFinder): CompleteSite = {
    val html =
      PimpWebHtml(assets.styles.map(_.to), assets.scripts.map(_.to) ++ assets.adhocScripts, routes, assetFinder)
    val fileMap = Map(
      routes.index -> html.index,
      routes.notFound -> html.notFound,
      routes.about -> html.about,
      routes.forum -> html.forum,
      routes.docs -> html.docWin,
      routes.docsDeb -> html.docDeb,
      routes.wp -> html.docWinPhone,
      routes.docsAlarms -> html.alarms,
      routes.legalPrivacy -> html.privacyPolicy,
      routes.downloads -> html.downloads(Home.releaseDate, Nil),
      routes.ping -> html.ping
    ).toList
    val pages = fileMap.map { case (route: Route, page: TagPage) => PageMapping(page, route.file) }
    assets.site(pages, Seq(ByteMapping(Json.toBytes(Json.toJson(VersionInfo.default)), routes.build.file)))
  }
}
