package com.malliina.pimpweb.tags

import com.malliina.file.FileUtilities
import com.malliina.pimpweb.tags.Bootstrap._
import controllers.routes.Assets.at
import controllers.routes.{Home => HomeRoute}
import controllers.{Docs, Home}
import models.PrivacyPolicy
import play.api.mvc.Call

import scalatags.Text.all._
import scalatags.Text.{GenericAttr, TypedTag}

object Tags {
  implicit val callAttr = new GenericAttr[Call]
  val titleTag = tag("title")
  val nav = tag("nav")

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
    headerRow("Mac"),
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
        li(aHref("http://java.com/en/download/index.jsp", "Java 8"), "."),
        li("A modern browser, such as ", aHref("http://windows.microsoft.com/en-us/internet-explorer/ie-10-worldwide-languages", "Internet Explorer 10"),
          " or a recent version of ", aHref("http://getfirefox.com", "Firefox"), " or ", aHref("http://www.google.com/chrome", "Chrome"), "."),
        li(aHref("http://www.microsoft.com/net/download", ".NET Framework"), " 3.5 or higher.")
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
            p(img(src := at("img/usage-folders2.png"), `class` := "img-responsive img-thumbnail"))),
          li("Open the ", strong("MusicPimp"), " app on your mobile device and add your PC as a music endpoint:",
            p(img(src := at("img/usage-wp8.png"), `class` := "img-responsive img-thumbnail"))),
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
        div(`class` := BtnGroup, attr("src-toggle") := "buttons-radio")(
          docLink("Windows", HomeRoute.win(), "win"),
          docLink("Mac", HomeRoute.mac(), "mac"),
          docLink("DEB", HomeRoute.deb(), "deb"),
          docLink("RPM", HomeRoute.rpm(), "rpm")
        )
      ),
      rowColumn(ColMd6)(inner)
    )
  }

  val privacyPolicy = indexMain("about")(
    headerRow("Privacy Policy", ColMd6),
    rowColumn(ColMd6)(
      p(PrivacyPolicy.text),
      p(PrivacyPolicy.purpose),
      p(PrivacyPolicy.roaming),
      p(PrivacyPolicy.network)
    )
  )

  def downloads(previous: Seq[String]) = indexMain("downloads")(
    headerRow("Downloads"),
    fullRow(
      leadPara("Download the server. It's free. No nonsense.")
    ),
    row(
      div4(
        h2(iClass("icon-windows"), " Windows"),
        leadPara("Download for Windows"),
        downloadLink(Home.msiDownload, s"primary $Btn-lg"),
        p("Released on 11 November 2016")
      ),
      div4(
        h2(iClass("icon-linux"), " Linux"),
        downloadLink(Home.debDownload),
        p("DEB packages are tested on Ubuntu."),
        downloadLink(Home.rpmDownload),
        p("RPM packages are tested on Fedora.")
      ),
      div4(
        h2(iClass("icon-apple"), " Mac"),
        downloadLink(Home.dmgDownload),
        p("OSX packages are tested on OSX Yosemite.")
      )
    ),
    fullRow(
      h2("What next?"),
      p("Install the software and navigate to ", aHref("http://localhost:8456/"),
        ". For more information, check the ", aHref(HomeRoute.win(), "documentation"), ".")
    ),
    rowColumn(ColMd6)(
      h3("Previous versions"),
      ul(
        previous map { prev =>
          liHref(Home.downloadBaseUrl + prev, prev)
        }
      )
    )
  )

  val index = indexNoContainer("home")(
    divClass(Jumbotron)(
      divClass(Container)(
        h1("MusicPimp", small(`class` := PullRight)("No ads. No social media. Pure music.")),
        leadPara("Control your music libraries with your phone. Play the music on your phone, home stereo system, in your car or stream it anywhere.")
      )
    ),
    divClass("container")(
      fullRow(
        h2("Get it"),
        a(`class` := s"$BtnPrimary centered", href := Home.msiDownload.url)(glyphIcon("download"), " Download for Windows"),
        leadPara(
          aHref(HomeRoute.downloads(), "Download"),
          " the free server for ", aHref(Home.msiDownload.url, "Windows"),
          " or ", aHref(Home.debDownload.url, "Linux"),
          ". Get the ", strong("MusicPimp"),
          " apps for ", aHref(Home.iosAppUri, "iOS"),
          ", ", aHref(Home.androidAppUri, "Android"),
          " and ", aHref(Home.amazonAppUri, "Kindle Fire"),
          ", ", aHref(Home.winPhoneAppUri, "Windows Phone"),
          " and ", aHref(Home.winStoreAppUri, "Windows 8"), "."
        )
      ),
      row(
        div4(
          a(href := Home.iosAppUri, `class` := s"$VisibleLg $VisibleMd $PullRight badge-ios")
        ),
        div4(
          badgeFromAssets(Home.androidAppUri, "Android app on Google Play", "en_app_rgb_wo_60.png"),
          badgeFromAssets(Home.amazonAppUri, "Android app on Amazon AppStore", "amazon-apps-kindle-us-gray.png")
        ),
        div4(
          badgeFromAssets(Home.winPhoneAppUri, "Windows Phone app", "badge_winphone2.png", PullLeft),
          badgeFromAssets(Home.winStoreAppUri, "Windows Store app", "badge_winstore.png", PullLeft)
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
          img(src := at("img/upload-alt-blue-128.png"), `class` := s"$PullLeft $VisibleLg $VisibleMd")
        )
      ),
      hr,
      row(
        feature("PC to Phone", "pc-phone.png", "Make the music library on a PC available for playback on your phone."),
        feature("Phone to PC", "phone-pc.png", "Play the music stored on your phone on speakers connected to a PC."),
        feature("PC to Phone to PC", "pc-phone-pc.png", "Play music from your PC on another PC. Control playback with your phone.")
      ),
      hr,
      row(
        divClass(s"$ColMd4 $ColMdOffset2")(
          h2("Getting Started"),
          leadPara(
            "Get started in minutes. Check the ",
            aHref(HomeRoute.win(), "documentation"),
            " for instructions."
          )
        ),
        divClass(ColMd4)(
          h2("Develop"),
          leadPara("Build cool apps using the JSON ", aHref(HomeRoute.api(), "API"), ".")
        )
      )
    )
  )

  val forum = indexMain("forum")(
    rowColumn(ColMd6)(
      divClass(PageHeader)(
        h1(
          "Forum ",
          small(
            "Visit this forum at ",
            a(href := "https://groups.google.com/forum/#!forum/musicpimp")(
              "groups.google.com ",
              glyphIcon("external-link")
            )
          )
        )
      )
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
        nav(`class` := s"$ColMd3 bs-docs-sidebar", id := "sidenav")(
          ul(`class` := s"$NavStacked affix", id := "sidebar")(
            liHref("#requests", "Requests"),
            liHref("#responses", "Responses"),
            li(
              aHref("#endpoints", "HTTP endpoints"),
              ulClass(NavStacked)(
                liHref("#library", "Library"),
                liHref("#player", "Player"),
                liHref("#playlist", "Playlist"),
                liHref("#alarms", "Alarms"),
                liHref("#misc", "Miscellaneous")
              )
            ),
            liHref("#server", "Server events")
          )
        )
      )
    )

  val about = indexMain("about")(
    headerRow("About"),
    divClass("row")(
      divClass(ColMd6)(
        p("Developed by ", aHref("https://mskogberg.info", "Michael Skogberg"), "."),
        p(img(src := at("img/handsome.png"), `class` := "img-responsive img-thumbnail")),
        p("Should you have any questions, don't hesitate to:",
          ul(
            li("contact ", aHref("mailto:info@musicpimp.org", "info@musicpimp.org")),
            li("post in the ", a(href := HomeRoute.forum())("forum ", glyphIcon("comment"))),
            li("open an issue on ", aHref("https://github.com/malliina/musicpimp/issues", "GitHub"))
          )
        )
      ),
      divClass(ColMd6)(
        p("This site uses icons by ", aHref("http://glyphicons.com/", "Glyphicons"), " and ", aHref("http://fontawesome.io/", "Font Awesome"), "."),
        p(a(href := "https://www.jetbrains.com/idea/")(img(src := at("img/logo_Jetbrains_3.png"), `class` := "img-responsive")))
      )
    )
  )

  def indexMain(tabName: String)(inner: Modifier*) = indexNoContainer(tabName)(
    divClass(Container)(inner)
  )

  def indexNoContainer(tabName: String)(inner: Modifier*) = {
    def navItem(thisTabName: String, tabId: String, url: Call, glyphiconName: String) = {
      val maybeActive = if (tabId == tabName) Option(`class` := "active") else None
      li(maybeActive)(a(href := url)(glyphIcon(glyphiconName), s" $thisTabName"))
    }

    plainMain("MusicPimp")(
      divClass(s"$Navbar $NavbarDefault")(
        divClass(Container)(
          divClass(NavbarHeader)(
            button(`class` := NavbarToggle, attr("data-toggle") := Collapse, attr("data-target") := s".$NavbarCollapse")(
              spanClass("icon-bar"),
              spanClass("icon-bar"),
              spanClass("icon-bar")
            ),
            a(`class` := NavbarBrand, href := HomeRoute.index())("MusicPimp")
          ),
          divClass(s"$NavbarCollapse $Collapse")(
            ulClass(s"$Nav $NavbarNav")(
              navItem("Home", "home", HomeRoute.index(), "home"),
              navItem("Downloads", "downloads", HomeRoute.downloads(), "download-alt"),
              navItem("Documentation", "documentation", HomeRoute.win(), "list-alt"),
              navItem("Forum", "forum", HomeRoute.forum(), "comment")
            ),
            ulClass(s"$Nav $NavbarNav $NavbarRight")(
              navItem("Develop", "api", HomeRoute.api(), "edit"),
              navItem("About", "about", HomeRoute.about(), "globe")
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
        link(rel := "shortcut icon", href := at("img/pimp-28.png")),
        css("//netdna.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css"),
        css("//netdna.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css"),
        css("//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css"),
        css(at("css/custom.css")),
        css(at("css/sidebar.css")),
        css(at("css/footer.css")),
        js("//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"),
        js("//netdna.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"),
        js(at("js/docs.js"))
      ),
      body(attr("data-spy") := "scroll", attr("data-target") := "#sidenav", attr("data-offset") := "200")(
        div(id := "wrap")(
          inner,
          div(id := "push")
        ),
        div(id := "footer")(
          divClass(Container)(
            pClass("muted credit pull-right")("Developed by ", a(href := "https://mskogberg.info")("Michael Skogberg"), ".")
          )
        )
      )
    )
  )

  def badgeFromAssets(link: String, altText: String, file: String, classes: String = "") =
    badge(link, altText, at(s"img/$file").toString, classes)

  def badge(link: String, altText: String, imgUrl: String, classes: String) =
    a(href := link, `class` := s"$VisibleLg $VisibleMd badge $classes")(img(alt := altText, src := imgUrl, `class` := "badge-image"))

  def feature(featureTitle: String, imgFile: String, leadText: String) =
    div4(
      h2(featureTitle),
      p(img(src := at(s"img/$imgFile"))),
      leadPara(leadText)
    )

  def downloadLink(dl: Home.Download, btnName: String = "default") =
    p(a(`class` := s"$Btn $Btn-$btnName", href := dl.url)(glyphIcon("download"), s" ${dl.fileName}"))

  def divClass(clazz: String) = div(`class` := clazz)

  def spanClass(clazz: String) = span(`class` := clazz)

  def iClass(clazz: String) = i(`class` := clazz)

  def leadPara = pClass("lead")

  def pClass(clazz: String) = p(`class` := clazz)

  def ulClass(clazz: String) = ul(`class` := clazz)

  def liHref(url: String, text: String) = li(aHref(url, text))

  def aHref(url: String): TypedTag[String] = aHref(url, url)

  def aHref[V: AttrValue](url: V, text: String): TypedTag[String] = a(href := url)(text)

  def js[V: AttrValue](url: V) = script(src := url)

  def css[V: AttrValue](url: V) = link(rel := "stylesheet", href := url)

  def markdown(docName: String): Modifier = Docs.toHtml(markdownAsString(docName)).map(RawFrag.apply)

  def markdownAsString(docName: String) =
    FileUtilities.readerFrom(s"docs/$docName.md")(_.mkString(FileUtilities.lineSep))
}