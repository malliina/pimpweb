package controllers

import com.mle.util.Log
import play.api.mvc.{Action, Controller}
import views.html
import play.api.templates.Html
import play.api.libs.json.Json
import com.mle.azure.{AzureStorageCredentialReader, StorageClient}
import java.net.URI

/**
 * @author Michael
 */
object Home extends Controller with Log {
  val downloadBaseUrl = "http://files.musicpimp.org/files/"
  val version = "1.6.0"
  val msiFileName = s"musicpimp-$version.msi"
  val debFileName = s"musicpimp-$version.deb"
  val rpmFileName = s"musicpimp-$version-0.noarch.rpm"
  val msiUrl = toUrl(msiFileName)
  val debUrl = toUrl(debFileName)
  val rpmUrl = toUrl(rpmFileName)
  val linuxConfFile = "/opt/musicpimp/musicpimp.conf"
  val windowsConfFile = """C:\Program Files (x86)\MusicPimp\musicpimp.conf"""
  val winPhoneAppUri = "http://windowsphone.com/s?appId=d31b505b-ac9f-4d93-8812-6b649734a5a6"
  val winStoreAppUri = "http://apps.microsoft.com/windows/en-us/app/musicpimp/73b9a42c-e38a-4edf-ac7e-00672230f7b6"

  val serverWebSocketResource = "/ws/playback"
  val serverPlaybackResource = "/playback"
  val webPlayWebSocketResource = "/ws/webplay"
  val webPlayPostResource = "/webplay"

  private def toUrl(fileName: String) = downloadBaseUrl + fileName

  def ping = Action(Ok(Json.obj("status" -> "ok")).withHeaders(CACHE_CONTROL -> "no-cache"))

  def index = GoTo(html.index())

  def downloads = GoTo(html.downloads(previousDownloadables))

  def previous = Action(Ok(Json.toJson(previousDownloadables)))

  def documentation = GoTo(html.docWin())

  def win = GoTo(html.docWin())

  def deb = GoTo(html.docDeb())

  def rpm = GoTo(html.docRpm())

  def wp = GoTo(html.docWinPhone())

  def api = GoTo(html.docApi())

  def forum = GoTo(html.forum())

  def about = GoTo(html.about())

  def complete = GoTo(html.success())

  def incomplete = GoTo(html.cancel())

  def success = Action(Redirect(routes.Home.complete()))

  def cancel = Action(Redirect(routes.Home.incomplete()))

  def privacyPolicy = GoTo(html.privacyPolicyStore())

  private def GoTo(page: Html) = Action(Ok(page))

  private def previousDownloadables =
    (downloadables filterNot isLatest).toSeq.reverse

  private def isLatest(fileName: String) =
    fileName == msiFileName || fileName == debFileName || fileName == rpmFileName

  private def downloadables = {
    val creds = AzureStorageCredentialReader.load
    val client = new StorageClient(creds.accountName, creds.accountKey)
    val uriStrings = client uris "files"
    uriStrings map fileName filter (_.startsWith("musicpimp"))
  }

  private def fileName(uri: URI) = {
    val uriString = uri.toString
    uriString.substring((uriString lastIndexOf '/') + 1, uriString.size)
  }
}
