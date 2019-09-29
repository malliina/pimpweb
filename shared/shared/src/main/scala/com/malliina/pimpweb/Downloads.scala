package com.malliina.pimpweb

case class Download(fileName: String) {
  val url = Downloads.toUrl(fileName)
}

object Downloads extends DownloadVersions {
  val downloadBaseUrl = "https://files.musicpimp.org/"

  def toUrl(fileName: String) = downloadBaseUrl + fileName
}

trait DownloadVersions {
  val winVersion = "4.20.3"
  val debVersion = "4.20.2"
  val msiDownload = Download(s"musicpimp-$winVersion.msi")
  val debDownload = Download(s"musicpimp_${debVersion}_all.deb")
  val releaseDate = "28 September 2019"
  val latestDownloads = Seq(msiDownload, debDownload)
}
