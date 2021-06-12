package org.musicpimp.generator

import com.malliina.generator.{AppMode, AssetFinder, BuiltPages, Generator, MappedAssets, PageMapping}

object MusicPimpGenerator extends Generator {
  override def pages(assets: MappedAssets, assetFinder: AssetFinder, mode: AppMode): BuiltPages = {
    val routes = if (mode == AppMode.Dev) DevRoutes else ProdRoutes
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
      routes.downloads -> html.downloads(Urls.releaseDate, Nil),
      routes.ping -> html.ping
    ).toList
    val pages: List[PageMapping] = fileMap.map {
      case (route, page) => PageMapping(page, route.file)
    }
    BuiltPages(pages, routes.index.name, routes.notFound.name)
  }
}
