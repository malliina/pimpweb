package org.musicpimp.generator

import com.malliina.generator.{Route, StorageKey}

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
}

object ProdRoutes extends PimpRoutes {
  def build(name: String) = Route(name)
  def index = Route(StorageKey("index"), "index.html", "/")
}

object DevRoutes extends PimpRoutes {
  def build(name: String) = Route.local(name)
  def index = Route.local("index")
}

abstract class PimpRoutes extends Routes {
  def build(name: String): Route
  def about = build("about")
  def docs = build("getting-started")
  def docsAlarms = build("getting-started/alarms")
  def docsDeb = build("getting-started/deb")
  def downloads = build("downloads")
  def forum = build("forum")
  def legalPrivacy = build("legal/privacy")
  def notFound = build("notfound")
  def ping = build("ping")
  def wp = build("getting-started/wp")
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
