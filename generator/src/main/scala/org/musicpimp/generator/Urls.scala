package org.musicpimp.generator

import com.malliina.pimpweb.DownloadVersions

object Urls extends DownloadVersions {
  val linuxConfFile = "/etc/musicpimp/application.ini"
  val windowsConfFile = """C:\Program Files (x86)\MusicPimp\musicpimp.conf"""
  val winPhoneAppUri = "https://www.windowsphone.com/s?appid=84cd9030-4a5c-4a03-b0ab-4d59c2fa7d42"
  val winStoreAppUri = "https://apps.microsoft.com/windows/en-us/app/musicpimp/73b9a42c-e38a-4edf-ac7e-00672230f7b6"
  val androidAppUri = "https://play.google.com/store/apps/details?id=org.musicpimp"
  val amazonAppUri = "https://www.amazon.com/gp/product/B00GVHTEJY/ref=mas_pm_musicpimp"
  val iosAppUri = "https://geo.itunes.apple.com/fi/app/musicpimp/id1074372634?mt=8"

  val serverWebSocketResource = "/ws/playback"
  val serverPlaybackResource = "/playback"
  val webPlayWebSocketResource = "/ws/webplay"
  val webPlayPostResource = "/webplay"
}
