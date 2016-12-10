package controllers

import java.net.URI

import com.malliina.azure.{AzureStorageCredentialReader, StorageClient}
import com.malliina.pimpweb.tags.Tags
import com.malliina.play.controllers.BaseController
import play.api.http.Writeable
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import views.html

object Home {

  case class Download(fileName: String) {
    val url = toUrl(fileName)
  }

  val downloadBaseUrl = "https://files.musicpimp.org/files/"
  val version = "3.3.0"
  val msiDownload = Download(s"musicpimp-$version.msi")
  val debDownload = Download(s"musicpimp_${version}_all.deb")
  val rpmDownload = Download(s"musicpimp-$version-1.noarch.rpm")
  val dmgDownload = Download(s"musicpimp-$version.dmg")
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

class Home extends Controller with BaseController {
  def ping = Action(NoCache(Ok))

  def index = GoTo(Tags.index)

  def downloads = GoTo(Tags.downloads(previousDownloadables))

  def previous = Action(Ok(Json.toJson(previousDownloadables)))

  def documentation = GoTo(html.docWin())

  def win = GoTo(html.docWin())

  def mac = GoTo(html.docMac())

  def deb = GoTo(html.docDeb())

  def rpm = GoTo(html.docRpm())

  def wp = GoTo(html.docWinPhone())

  def api = GoTo(Tags.api)

  def alarms = GoTo(html.alarms())

  def forum = GoTo(Tags.forum)

  def about = GoTo(Tags.about)

  def complete = GoTo(html.success())

  def incomplete = GoTo(html.cancel())

  def success = Action(Redirect(routes.Home.complete()))

  def cancel = Action(Redirect(routes.Home.incomplete()))

  def privacyPolicy = GoTo(html.privacyPolicyStore())

  private def GoTo[C: Writeable](page: C) = Action(Ok(page))

  private def previousDownloadables =
    (downloadables filterNot isLatest).toSeq.reverse

  private def isLatest(fileName: String) =
    Home.latestDownloads.exists(_.fileName == fileName)

  private def downloadables: Iterable[String] = {
    val maybeFiles = AzureStorageCredentialReader.loadOpt.map { creds =>
      val client = new StorageClient(creds.accountName, creds.accountKey)
      val uriStrings = client uris "files"
      uriStrings map fileName filter (_.startsWith("musicpimp"))
    }
    maybeFiles getOrElse Nil
  }

  private def fileName(uri: URI) = {
    val uriString = uri.toString
    uriString.substring((uriString lastIndexOf '/') + 1, uriString.length)
  }
}
