package org.musicpimp.generator

import play.api.libs.json.Json

object PrivacyPolicy {
  val text = "This privacy policy describes how your information is used and stored when you use this app."
  val purpose =
    "The purpose of using and storing your information is to enable app functionality and optimize your user experience."
  val roaming =
    "Roaming data: This app uses roaming application data for Windows Store apps to store playlists, app-wide settings and information about any music endpoints you may have added to the app. Endpoint passwords are encrypted."
  val network =
    "Network communications: This app may communicate with other networked servers should you decide to add music endpoints to the app. The MusicPimp server, MusicBeamer or third-party servers (i.e. Subsonic) may log any requests made towards them. Tracks from your music library may be streamed or uploaded to any MusicPimp endpoints you are using. Track meta data may be retrieved from third party data stores such as Discogs."
  val paragraphs = Seq(text, purpose, roaming, network)
  val json = Json.obj("paragraphs" -> paragraphs)
}
