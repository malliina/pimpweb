package com.malliina.pimpweb

case class Download(fileName: String) {
  val url = Downloads.toUrl(fileName)
}

object Downloads extends DownloadVersions {
  val downloadBaseUrl = "https://files.musicpimp.org/"

  def toUrl(fileName: String) = downloadBaseUrl + fileName
}

trait DownloadVersions {
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
}
