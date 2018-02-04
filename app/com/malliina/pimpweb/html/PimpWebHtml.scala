package com.malliina.pimpweb.html

import controllers.routes.PimpAssets.versioned
import com.malliina.file.FileUtilities
import com.malliina.pimpweb.assets.AppAssets
import com.malliina.tags.Bootstrap._
import com.malliina.tags.TagPage
import com.malliina.tags.Tags._
import controllers.{Docs, Home}
import models.PrivacyPolicy
import play.api.mvc.Call

import scalatags.Text.GenericAttr
import scalatags.Text.all._

object PimpWebHtml {
  implicit val callAttr = new GenericAttr[Call]
  val titleTag = tag("title")
  val nav = tag("nav")

  val images = AppAssets.img
  val homeRoute = controllers.routes.Home

  val linuxReqs: Modifier = Seq(
    h2("System Requirements"),
    ul(
      li("Java 8. Oracle Java is recommended."),
      li("A modern browser.")
    )
  )

  val rpmInstall = Seq(
    headerRow("RPM"),
    fullRow(
      linuxReqs,
      h2("Installation"),
      p("Download ", aHref(Home.rpmDownload.url, Home.rpmDownload.fileName), " and execute: ", code(s"rpm -ivh ${Home.rpmDownload.fileName}"))
    )
  )

  val rpmUninstall = p(code("rpm -e musicpimp"))

  val docRpm = docuBase("rpm", Home.linuxConfFile, rpmInstall, rpmUninstall)

  val debInstall: Modifier = Seq(
    headerRow("DEB"),
    fullRow(
      linuxReqs,
      h2("Installation"),
      p("Download ", aHref(Home.debDownload.url, Home.debDownload.fileName), " and execute: ", code(s"dpkg -i ${Home.debDownload.fileName}"))
    )
  )

  val debUninstall = p(code("apt-get purge musicpimp"))

  val docDeb = docuBase("deb", Home.linuxConfFile, debInstall, debUninstall)

  val docMac = docPlain("mac")(
    headerRow("MacOS"),
    fullRow(
      h2("Installation"),
      p("Download ", aHref(Home.dmgDownload.url, Home.dmgDownload.fileName), ", open and install. MusicPimp installs" +
        " itself as a launchd system daemon. Use ", strong("launchctl"), " to adjust that behavior if needed.")
    )
  )

  val winInstall: Modifier = Seq(
    headerRow("Windows"),
    fullRow(
      h2("System Requirements"),
      ul(
        li(aHref("http://java.com/en/download/index.jsp", "Java 8")),
        li("A modern browser, such as ", aHref("http://getfirefox.com", "Firefox"), " or ", aHref("http://www.google.com/chrome", "Chrome")),
        li(aHref("http://www.microsoft.com/net/download", ".NET Framework"), " 3.5 or higher")
      ),
      h2("Installation"),
      p("Download and run ", aHref(Home.msiDownload.url, Home.msiDownload.fileName), ".")
    )
  )

  val winUninstall = p("Uninstall MusicPimp using the Add/Remove Programs section in the Control Panel.")

  val docWin = docuBase("win", Home.windowsConfFile, winInstall, winUninstall)

  def docuBase(os: String, confPath: String, installation: Modifier, uninstallation: Modifier) =
    docPlain(os)(
      fullRow(
        installation,
        h2("Usage"),
        ol(
          li("Navigate to ", aHref("http://localhost:8456/"), " and login."),
          li("Select tab ", strong("Manage"), " and specify folders containing MP3s under ", strong("Music Folders"), ":",
            p(img(src := images.usage_folders2_png, `class` := "img-responsive img-thumbnail"))),
          li("Open the ", strong("MusicPimp"), " app on your mobile device and add your PC as a music endpoint:",
            p(img(src := images.usage_wp8_png, `class` := "img-responsive img-thumbnail"))),
          li("Enjoy your music.")
        ),
        h2("Supported Audio Formats"),
        p("MusicPimp supports MP3 playback."),
        h2("Connectivity"),
        p("Music is streamed over WLAN, mobile networks or Bluetooth."),
        h2("Advanced Configuration"),
        p("Advanced settings can be configured in ", code("musicpimp.conf"),
          " in your installation directory. The path is typically ", code(confPath), ". Reasonable defaults are provided."),
        h4("HTTPS"),
        p("To enable HTTPS, specify the following parameters in ", code("musicpimp.conf"), ":"),
        table(`class` := "table table-hover")(
          thead,
          tr(th("Key"), th("Value")),
          tbody(
            tr(td("https.port"), td("The HTTPS port to use")),
            tr(td("https.keyStore"), td("The path to the keystore")),
            tr(td("https.keyStorePassword"), td("The keystore password")),
            tr(td("https.keyStoreType"), td("Optionally, the keystore type. Defaults to JKS."))
          )
        ),
        p("A keystore with a self-signed certificate is included in the MusicPimp software distributions in the folder ",
          code("config/security/test-cert.jks"), " under the installation directory. The keystore passphrase is ", strong("musicpimp"), "."),
        p(spanClass("label label-info")("Note"), " In order to successfully connect to the MusicPimp (or Subsonic) server " +
          "over HTTPS using Windows Phone, your server certificate must be trusted by your phone. " +
          "This means self-signed certificates are unlikely to work."),
        h2("Logging"),
        p("MusicPimp writes a log to the directory given by the ", code("log.dir"), " system property. At midnight, the " +
          "log from the previous day is archived to a separate file and a new file is written for the following day. " +
          "The active log file is also archived if it reaches 100 MB in size (during the same day.) " +
          "Logs older than 30 days are finally deleted."),
        h2("Uninstallation"),
        uninstallation
      )
    )

  def docPlain(os: String)(inner: Modifier*) = {
    def docLink(text: String, clicked: Call, osId: String) = {
      val suffix = if (osId == os) " active" else ""
      button(`type` := "button", `class` := s"$BtnPrimary$suffix", onclick := s"location.href='$clicked'")(text)
    }

    indexMain("documentation")(
      headerRow("Documentation", ColMd6),
      rowColumn(ColMd6)(
        div(`class` := s"$BtnGroup last-box", attr("src-toggle") := "buttons-radio")(
          docLink("Windows", homeRoute.win(), "win"),
          docLink("MacOS", homeRoute.mac(), "mac"),
          docLink("DEB", homeRoute.deb(), "deb"),
          docLink("RPM", homeRoute.rpm(), "rpm")
        )
      ),
      rowColumn(ColMd8)(inner)
    )
  }

  val docWinPhone = docPlain("wp")(
    headerRow("Windows Phone"),
    fullRow(
      h2("System Requirements"),
      p("Windows Phone 7.5 and higher are supported."),
      h2("Installation"),
      p("Install the ", strong("MusicPimp"), " ", aHref(Home.winPhoneAppUri, "app"), " to your Windows Phone device."),
      h2("Usage"),
      ol(
        li("Add music endpoints to your app. Any PC on which MusicPimp is installed works as a music endpoint."),
        li("Set the PC as the music source or use it for music playback in the app.")
      ),
      h3("HTTPS"),
      p("You may wish to connect to your music server over HTTPS. HTTPS is " +
        "currently supported on Windows Phone provided that the server " +
        "certificate passes validation and is trusted by your phone. This " +
        "means that self-signed certificates will most likely not work. " +
        "Commercial certificates are likely to work."),
      h3("Miscellaneous"),
      p("MusicPimp for Windows Phone also supports the ",
        aHref("http://www.subsonic.org", "Subsonic"),
        " media streamer as a music server.")
    )
  )

  val privacyPolicy = indexMain("about")(
    headerRow("Privacy Policy", ColMd6),
    rowColumn(ColMd6)(
      p(PrivacyPolicy.text),
      p(PrivacyPolicy.purpose),
      p(PrivacyPolicy.roaming),
      p(PrivacyPolicy.network)
    )
  )

  val alarms = indexMain("alarms")(
    headerRow("Alarm clock management"),
    rowColumn(ColMd6)(
      leadPara("Control the alarm clock on the MusicPimp server using the following API."),
      h4("Get all alarms ", small("GET /alarms")),
      p("Get an array of the currently configured alarms."),
      p("Example response:"),
      pre(
        """[
          |    {
          |        "id":"f1bf52c6-eb9e-4407-8b8e-91444a78c14c",
          |        "job":{
          |            "track":{
          |                "id":"Paola+-+Interstellar+Love.mp3",
          |                "title":"Interstellar Love",
          |                "artist":"Paola",
          |                "album":"Stockcity Girl",
          |                "duration":201,
          |                "size":4840094
          |            }
          |        },
          |        "when":{
          |            "hour":8,
          |            "minute":13,
          |            "days":["mon","tue","wed","thu","fri","sat","sun"]
          |        },
          |        "enabled":true
          |    }
          |]
        """.stripMargin),
      h4("Get alarm ", small("GET /alarms/alarm_id_here")),
      p("Get an alarm with a given ID."),
      h4("Remove alarm ", small("HTTP POST to /alarms")),
      p("Remove an alarm:"),
      code("""{"cmd": "delete", "id": "alarm_id_here"}"""),
      h4("Add or update an alarm ", small("HTTP POST to /alarms")),
      p("Add a new alarm or update an existing one. When updating, " +
        "include the ID of the alarm you update. If no ID is provided, " +
        "the interpretation is that you add a new alarm; the server will " +
        "generate a new ID."),
      pre(
        """{
          |    "cmd":"save",
          |    "ap":{
          |        "id":"f1bf52c6-eb9e-4407-8b8e-91444a78c14c",
          |        "job":{
          |            "track":"Paola+-+Interstellar+Love.mp3"
          |        },
          |        "when":{
          |            "hour":16,
          |            "minute":24,
          |            "days":["sun","mon","tue","wed","thu","fri","sat"]
          |        },
          |        "enabled":true
          |    }
          |}
        """.stripMargin),
      h4("Start an alarm ", small("HTTP POST to /alarms")),
      p("Manually start playback of an alarm with a given ID. Useful for testing."),
      code("""{"cmd": "start", "id": "alarm_id_here"}"""),
      h4("Stop an alarm ", small("HTTP POST to /alarms")),
      p("Stop any alarm currently playing."),
      code("""{"cmd": "stop"}""")
    )
  )

  val success = indexMain("success")(
    leadPara(iconic("thumb-up"), " Thanks for your donation!"),
    p("Should you have any questions, don't hesitate to contact ", aHref("mailto:info@musicpimp.org", "info@musicpimp.org")),
    p("Best regards! Michael Skogberg, MusicPimp")
  )

  val cancel = indexMain("cancel")(
    leadPara(glyphIcon("fire"), " The donation procedure was prematurely canceled. ", iconic("fire")),
    p("Should you have any questions, don't hesitate to contact ", aHref("mailto:info@musicpimp.org", "info@musicpimp.org"))
  )

  def downloads(releaseDate: String, previous: Seq[String]) = indexMain("downloads")(
    headerRow("Downloads"),
    fullRow(
      leadPara("Download the server. It's free. No nonsense.")
    ),
    row(
      div4(
        h2(iClass("icon-windows"), " Windows"),
        downloadLink(Home.msiDownload, s"primary $Btn-lg"),
        p(s"Released on $releaseDate.")
      ),
      div4(
        h2(iClass("icon-linux"), " Linux"),
        downloadLink(Home.debDownload),
        p("DEB packages are tested on Ubuntu."),
        downloadLink(Home.rpmDownload),
        p("RPM packages are tested on Fedora.")
      ),
      div4(
        h2(iClass("icon-apple"), " MacOS"),
        downloadLink(Home.dmgDownload),
        p("OSX packages are tested on OSX Yosemite.")
      )
    ),
    fullRow(
      h2("What next?"),
      p("Install the software and navigate to ", aHref("http://localhost:8456/"),
        ". For more information, check the ", aHref(homeRoute.win(), "documentation"), ".")
    ),
    rowColumn(ColMd6)(
      h3("Previous versions"),
      ul(`class` := "pimp-list")(
        previous map { prev =>
          liHref(Home.downloadBaseUrl + prev, prev)
        }
      )
    )
  )

  val index = indexNoContainer("home")(
    divClass(Jumbotron)(
      divClass(s"$Container hero-section")(
        h1("MusicPimp"),
        h2("No ads. No social media. Pure music"),
        leadPara("Control your music libraries with your phone. Play the music on your phone, home stereo system, in your car or stream it anywhere.")
      )
    ),
    divClass("container")(
      fullRow(
        h2(`class` := "centered", "Get it")
      ),
      row(
        div(`class` := "col-md-8 col-md-offset-2")(
          ol(`class` := "pimp-list get-it")(
            li(
              aHref(homeRoute.downloads(), "Download"),
              " the free server for ", aHref(Home.msiDownload.url, "Windows"),
              ", ", aHref(Home.debDownload.url, "Linux"),
              " or ", aHref(Home.dmgDownload.url, "MacOS"), ".",
            ),
            li(
              "Get the ", strong("MusicPimp"),
              " apps for ", aHref(Home.iosAppUri, "iOS"),
              ", ", aHref(Home.androidAppUri, "Android"),
              " and ", aHref(Home.amazonAppUri, "Kindle Fire"),
              ", ", aHref(Home.winPhoneAppUri, "Windows Phone"),
              " and ", aHref(Home.winStoreAppUri, "Windows 8"), "."
            )
          )
        ),
      ),
      row(
        div4(
          a(href := Home.iosAppUri, `class` := s"$VisibleLg $VisibleMd $PullRight badge-ios")
        ),
        div4(
          badgeFromAssets(Home.androidAppUri, "Android app on Google Play", images.en_app_rgb_wo_60_png),
          badgeFromAssets(Home.amazonAppUri, "Android app on Amazon AppStore", images.amazon_apps_kindle_us_gray_png)
        ),
        div4(
          badgeFromAssets(Home.winPhoneAppUri, "Windows Phone app", images.badge_winphone2_png, PullLeft),
          badgeFromAssets(Home.winStoreAppUri, "Windows Store app", images.badge_winstore_png, PullLeft)
        )
      ),
      hr,
      row(
        divClass(s"$ColMd4 $ColMdOffset2")(
          h2("MusicBeamer"),
          leadPara(
            "Stream tracks from your music library to any PC using ",
            strong("MusicBeamer"), " at ", aHref("https://beam.musicpimp.org"), "."
          )
        ),
        div4(
          img(src := images.upload_alt_blue_128_png, `class` := s"$PullLeft $VisibleLg $VisibleMd")
        )
      ),
      hr,
      row(
        feature("PC to Phone", images.pc_phone_png, "Make the music library on a PC available for playback on your phone."),
        feature("Phone to PC", images.phone_pc_png, "Play the music stored on your phone on speakers connected to a PC."),
        feature("PC to Phone to PC", images.pc_phone_pc_png, "Play music from your PC on another PC. Control playback with your phone.")
      ),
      hr,
      row(
        divClass(s"$ColMd4 $ColMdOffset2")(
          h2("Getting Started"),
          leadPara(
            "Get started in minutes. Check the ",
            aHref(homeRoute.win(), "documentation"),
            " for instructions."
          )
        ),
        divClass(ColMd4)(
          h2("Develop"),
          leadPara("Build cool apps using the JSON ", aHref(homeRoute.api(), "API"), ".")
        )
      )
    )
  )

  val forum = indexMain("forum")(
    rowColumn(ColMd6)(
      divClass(PageHeader)(
        h1("Forum")
      )
    ),
    fullRow(
      "Visit this forum at ",
      a(href := "https://groups.google.com/forum/#!forum/musicpimp")("groups.google.com ", iconic("external-link")), "."
    )
  )

  val api = docApi(
    markdown("Requests"),
    markdown("Responses"),
    markdown("HttpEndpoints"),
    markdown("ServerEvents")
  )

  def docApi(requests: Modifier, responses: Modifier, httpEndpoints: Modifier, serverEvents: Modifier) =
    indexMain("api")(
      headerRow("Develop"),
      row(
        divClass(ColMd9)(
          leadPara("Develop apps for MusicPimp using the JSON API.")
        )
      ),
      row(
        divClass(ColMd8)(
          requests,
          responses,
          httpEndpoints,
          serverEvents
        ),
        nav(`class` := s"$ColMd3 side-nav bs-docs-sidebar", id := "sidenav")(
          nav(`class` := s"nav flex-column", id := "sidebar")(
            sideLink("#requests", "Requests"),
            sideLink("#responses", "Responses"),
            sideLink("#endpoints", "HTTP endpoints"),
            nav(`class` := s"nav flex-column")(
              sideLink("#library", "Library"),
              sideLink("#player", "Player"),
              sideLink("#playlist", "Playlist"),
              sideLink("#alarms", "Alarms"),
              sideLink("#misc", "Miscellaneous")
            ),
            sideLink("#server", "Server events")
          )
        )
      )
    )

  def sideLink(anchor: String, text: String) = a(href := anchor, `class` := "nav-link")(text)

  val about = indexMain("about")(
    headerRow("About"),
    row(
      divClass(ColMd6)(
        p("Developed by ", aHref("https://github.com/malliina", "Michael Skogberg"), "."),
        p(img(src := images.beauty_png, `class` := "img-responsive img-thumbnail")),
        p("Should you have any questions, don't hesitate to:",
          ul(
            li("contact ", aHref("mailto:info@musicpimp.org", "info@musicpimp.org")),
            li("post in the ", a(href := homeRoute.forum())("forum ", glyphIcon("comment"))),
            li("open an issue on ", aHref("https://github.com/malliina/musicpimp/issues", "GitHub"))
          )
        )
      ),
      divClass(ColMd6)(
        p("This site uses icons by ", aHref("https://useiconic.com/open", "Open Iconic"), "."),
        p("Developed with ", a(href := "https://www.jetbrains.com/idea/")("IntelliJ IDEA"), "."),
        p(a(href := "https://www.jetbrains.com/idea/")(img(src := images.logo_JetBrains_3_png, `class` := "img-responsive")))
      )
    )
  )

  def indexMain(tabName: String)(inner: Modifier*) = indexNoContainer(tabName)(
    divClass(s"$Container page-content")(inner)
  )

  def indexNoContainer(tabName: String)(inner: Modifier*) = {
    def navItem(thisTabName: String, tabId: String, url: Call, iconicName: String) = {
      val itemClass = if (tabId == tabName) "nav-item active" else "nav-item"
      li(`class` := itemClass)(a(href := url, `class` := "nav-link")(iconic(iconicName), s" $thisTabName"))
    }

    plainMain("MusicPimp")(
      nav(`class` := s"$Navbar navbar-expand-lg navbar-light bg-light")(
        divClass(Container)(
          divClass(NavbarHeader)(
            a(`class` := NavbarBrand, href := homeRoute.index())("MusicPimp"),
            button(`class` := "navbar-toggler", attr("data-toggle") := Collapse, attr("data-target") := s".$NavbarCollapse", aria.controls := "navbarSupportedContent", aria.expanded := "false", aria.label := "Toggle navigation")(
              spanClass("navbar-toggler-icon")
            )
          ),
          div(`class` := s"$Collapse $NavbarCollapse", id := "navbarSupportedContent")(
            ulClass(s"$NavbarNav mr-auto")(
              navItem("Home", "home", homeRoute.index(), "home"),
              navItem("Downloads", "downloads", homeRoute.downloads(), "data-transfer-download"),
              navItem("Documentation", "documentation", homeRoute.win(), "document"),
              navItem("Forum", "forum", homeRoute.forum(), "comment-square")
            ),
            ulClass(s"$Nav $NavbarNav $NavbarRight")(
              navItem("Develop", "api", homeRoute.api(), "pencil"),
              navItem("About", "about", homeRoute.about(), "globe")
            )
          )
        )
      ),
      inner
    )
  }

  def plainMain(pageTitle: String)(inner: Modifier*) = TagPage(
    html(lang := "en")(
      head(
        titleTag(pageTitle),
        meta(name := "viewport", content := "width=device-width, initial-scale=1.0"),
        link(rel := "shortcut icon", href := images.pimp_28_png),
        cssLinkHashed("https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css", "sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"),
        cssLink("https://use.fontawesome.com/releases/v5.0.6/css/all.css"),
        cssLink(versioned("css/main.css")),
        jsHashed("https://code.jquery.com/jquery-3.2.1.slim.min.js", "sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"),
        jsHashed("https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js", "sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"),
        jsHashed("https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js", "sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl")
      ),
      body(attr("data-spy") := "scroll", attr("data-target") := "#sidenav", attr("data-offset") := "200")(
        inner,
        footer(`class` := "footer")(
          divClass(Container)(
            spanClass("text-muted float-right")("Developed by ", a(href := "https://www.musicpimp.org")("Michael Skogberg"), ".")
          )
        )
      )
    )
  )

  def badgeFromAssets(link: String, altText: String, call: Call, classes: String = "") =
    badge(link, altText, call, classes)

  def badge(link: String, altText: String, imgUrl: Call, classes: String) =
    a(href := link, `class` := s"$VisibleLg $VisibleMd badge $classes")(
      img(alt := altText, src := imgUrl.toString, `class` := "badge-image")
    )

  def feature(featureTitle: String, imgFile: Call, leadText: String) =
    div4(
      h2(featureTitle),
      p(img(src := imgFile)),
      leadPara(leadText)
    )

  def downloadLink(dl: Home.Download, btnName: String = "primary") =
    p(a(`class` := s"$Btn $Btn-$btnName", href := dl.url)(iconic("data-transfer-download"), s" ${dl.fileName}"))

  def markdown(docName: String): Modifier = RawFrag(Docs.toHtml(markdownAsString(docName)))

  def markdownAsString(docName: String) =
    FileUtilities.readerFrom(s"docs/$docName.md")(_.mkString(FileUtilities.lineSep))
}
