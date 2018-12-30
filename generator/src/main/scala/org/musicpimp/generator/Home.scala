package org.musicpimp.generator

object Home {

  case class Download(fileName: String) {
    val url = toUrl(fileName)
  }

  val downloadBaseUrl = "https://files.musicpimp.org/"
  val winVersion = "3.10.0"
  val debVersion = "3.10.7"
  val rpmVersion = "3.6.3"
  val macVersion = "3.10.0"
  val msiDownload = Download(s"musicpimp-$winVersion.msi")
  val debDownload = Download(s"musicpimp_${debVersion}_all.deb")
  val rpmDownload = Download(s"musicpimp-$rpmVersion-1.noarch.rpm")
  val dmgDownload = Download(s"musicpimp-$macVersion.dmg")
  val releaseDate = "28 October 2017"
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
