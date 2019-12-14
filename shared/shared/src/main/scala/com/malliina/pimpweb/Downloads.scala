package com.malliina.pimpweb

case class Download(fileName: String) {
  val url = Downloads.toUrl(fileName)
}

object Downloads extends DownloadVersions {
  val downloadBaseUrl = "https://github.com/malliina/musicpimp/releases/download/v4.20.5/"

  def toUrl(fileName: String) = s"$downloadBaseUrl$fileName"
}

trait DownloadVersions {
  val winVersion = "4.20.5"
  val debVersion = "4.20.5"

  val msiDownload = Download(s"musicpimp-$winVersion.msi")
  val debDownload = Download(s"musicpimp_${debVersion}_all.deb")
  val releaseDate = "10 December 2019"
  val latestDownloads = Seq(msiDownload, debDownload)
}
