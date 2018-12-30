package org.musicpimp.generator

import scalatags.Text
import scalatags.Text.all._
import scalatags.text.Builder

trait Routes {
  val about: Route
  val docs: Route
  val docsAlarms: Route
  val docsApi: Route
  val docsDeb: Route
  val docsMac: Route
  val docsRpm: Route
  val downloads: Route
  val forum: Route
  val index: Route
  val legalPrivacy: Route
  val wp: Route
  val notFound: Route
}

case class Route(name: String, file: String, uri: String)

object Route {

  def apply(name: String): Route = Route(name, s"$name.html", s"/$name")

  implicit val v: AttrValue[Route] =
    (t: Builder, a: Text.Attr, v: Route) =>
      t.setAttr(a.name, Builder.GenericAttrValueSource(v.uri))
}

object SiteRoutes extends SiteRoutes

trait SiteRoutes extends Routes {
  val about = Route("about")
  val docs = Route("docs")
  val docsAlarms = Route("docs/alarms")
  val docsApi = Route("docs/api")
  val docsDeb = Route("docs/deb")
  val docsMac = Route("docs/mac")
  val docsRpm = Route("docs/rpm")
  val downloads = Route("downloads")
  val forum = Route("forum")
  val index = Route("index", "index.html", "/")
  val legalPrivacy = Route("legal/privacy")
  val notFound = Route("notfound")
  val wp = Route("docs/wp")
}

object Images extends Images

trait Images {
  val amazon_apps_kindle_us_gray_png = "/img/amazon-apps-kindle-us-gray.png"
  val badge_winphone2_png = "/img/badge_winphone2.png"
  val badge_winstore_png = "/img/badge_winstore.png"
  val beauty_png = "/img/beauty.png"
  val en_app_rgb_wo_60_png = "/img/en_app_rgb_wo_60.png"
  val logo_JetBrains_3_png = "/img/logo_JetBrains_3.png"
  val pc_phone_png = "/img/pc-phone.png"
  val pc_phone_pc_png = "/img/pc-phone-pc.png"
  val phone_pc_png = "/img/phone-pc.png"
  val pimp_28_png = "/img/pimp-28.png"
  val usage_folders2_png = "/img/usage-folders2.png"
  val usage_wp8_png = "/img/usage-wp8.png"
  val upload_alt_blue_128_png = "/img/upload-alt-blue-128.png"
}
