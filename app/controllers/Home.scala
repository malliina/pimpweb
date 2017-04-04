package controllers

import com.malliina.pimpweb.{BuildInfo, FileStore}
import com.malliina.pimpweb.tags.PimpWebHtml
import com.malliina.play.controllers.BaseController
import play.api.http.Writeable
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import views.html

object Home {

  case class Download(fileName: String) {
    val url = toUrl(fileName)
  }

  val downloadBaseUrl = "https://s3-eu-west-1.amazonaws.com/musicpimp-files/"
  val version = "3.6.3"
  val macVersion = "3.3.0"
  val msiDownload = Download(s"musicpimp-$version.msi")
  val debDownload = Download(s"musicpimp_${version}_all.deb")
  val rpmDownload = Download(s"musicpimp-$version-1.noarch.rpm")
  val dmgDownload = Download(s"musicpimp-$macVersion.dmg")
  val releaseDate = "26 March 2017"
  val latestDownloads = Seq(msiDownload, debDownload, rpmDownload, dmgDownload)
  val linuxConfFile = "/etc/musicpimp/application.ini"
  val windowsConfFile = """C:\Program Files (x86)\MusicPimp\musicpimp.conf"""
  val winPhoneAppUri = "https://www.windowsphone.com/s?appid=84cd9030-4a5c-4a03-b0ab-4d59c2fa7d42"
  val winStoreAppUri = "http://apps.microsoft.com/windows/en-us/app/musicpimp/73b9a42c-e38a-4edf-ac7e-00672230f7b6"
  val androidAppUri = "https://play.google.com/store/apps/details?id=org.musicpimp"
  val amazonAppUri = "http://www.amazon.com/gp/product/B00GVHTEJY/ref=mas_pm_musicpimp"
  val iosAppUri = "https://geo.itunes.apple.com/fi/app/musicpimp/id1074372634?mt=8"

  val serverWebSocketResource = "/ws/playback"
  val serverPlaybackResource = "/playback"
  val webPlayWebSocketResource = "/ws/webplay"
  val webPlayPostResource = "/webplay"

  private def toUrl(fileName: String) = downloadBaseUrl + fileName
}

class Home(fileStore: FileStore) extends Controller with BaseController {
  def ping = Action(NoCache(Ok(Json.obj("version" -> BuildInfo.version))))

  def index = GoTo(PimpWebHtml.index)

  def downloads = GoTo(PimpWebHtml.downloads(Home.releaseDate, previousDownloadables))

  def previous = Action(Ok(Json.toJson(previousDownloadables)))

  def documentation = win

  def win = GoTo(PimpWebHtml.docWin)

  def mac = GoTo(PimpWebHtml.docMac)

  def deb = GoTo(PimpWebHtml.docDeb)

  def rpm = GoTo(PimpWebHtml.docRpm)

  def wp = GoTo(html.docWinPhone())

  def api = GoTo(PimpWebHtml.api)

  def alarms = GoTo(html.alarms())

  def forum = GoTo(PimpWebHtml.forum)

  def about = GoTo(PimpWebHtml.about)

  def complete = GoTo(html.success())

  def incomplete = GoTo(html.cancel())

  def success = Action(Redirect(routes.Home.complete()))

  def cancel = Action(Redirect(routes.Home.incomplete()))

  def privacyPolicy = GoTo(PimpWebHtml.privacyPolicy)

  private def GoTo[C: Writeable](page: C) = Action(Ok(page))

  private def previousDownloadables =
    (fileStore.filenames filterNot isLatest).reverse

  private def isLatest(fileName: String) =
    Home.latestDownloads.exists(_.fileName == fileName)
}
