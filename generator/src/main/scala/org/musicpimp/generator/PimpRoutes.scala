package org.musicpimp.generator

import scalatags.Text
import scalatags.Text.all._
import scalatags.text.Builder

trait Routes {
  def about: Route
  def docs: Route
  def docsAlarms: Route
  def docsDeb: Route
  def downloads: Route
  def forum: Route
  def index: Route
  def legalPrivacy: Route
  def notFound: Route
  def ping: Route
  def wp: Route
  def build: Route
}

case class Route(name: StorageKey, file: String, uri: String)

object Route {
  def apply(name: String): Route = Route(StorageKey(name), s"$name.html", s"/$name")
  def local(name: String): Route = Route(StorageKey(name), s"$name.html", s"/$name.html")
  def simple(name: String): Route = Route(StorageKey(name), name, s"/$name")

  implicit val v: AttrValue[Route] =
    (t: Builder, a: Text.Attr, v: Route) => t.setAttr(a.name, Builder.GenericAttrValueSource(v.uri))
}

object ProdRoutes extends PimpRoutes(true)

object DevRoutes extends PimpRoutes(false)

class PimpRoutes(isProd: Boolean) extends Routes {
  def build(name: String) = if (isProd) Route(name) else Route.local(name)

  val about = build("about")
  val docs = build("docs")
  val docsAlarms = build("docs/alarms")
  val docsDeb = build("docs/deb")
  val downloads = build("downloads")
  val forum = build("forum")
  val index = if (isProd) Route(StorageKey("index"), "index.html", "/") else Route.local("index")
  val legalPrivacy = build("legal/privacy")
  val notFound = build("notfound")
  val ping = build("ping")
  val wp = build("docs/wp")
  val build = Route.simple("build.json")
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
