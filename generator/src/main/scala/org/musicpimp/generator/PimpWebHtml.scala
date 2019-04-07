package org.musicpimp.generator

import com.malliina.html.{Bootstrap, HtmlTags}
import com.malliina.pimpweb.{Download, FrontKeys}
import org.musicpimp.generator.PimpWebHtml.{Page, PageConf, PageTitle, subHeader}
import scalatags.Text.all._

object PimpWebHtml {
  val PageTitle = "MusicPimp"
  val subHeader = "No ads. No social media. Pure music."

  def apply(css: Seq[String], js: Seq[String], routes: Routes): PimpWebHtml = new PimpWebHtml(css, js, routes)

  trait PageConf {
    def title: String

    def bodyClasses: Seq[String]
  }

  case class Page(title: String, bodyClasses: Seq[String]) extends PageConf

  object Page {
    def default = Page(PageTitle, Nil)
  }

}

class PimpWebHtml(css: Seq[String], js: Seq[String], homeRoute: Routes) extends Bootstrap(HtmlTags) {

  import tags._

  val DocsUrl = "https://docs.musicpimp.org"

  val titleTag = tag("title")
  val defer = attr("defer").empty
  val images = Images

  def aHref(url: String): Modifier = aHref(url, url)

  def aHref[V: AttrValue](url: V, text: String): Modifier = a(href := url)(text)

  val index = indexNoContainer("home")(
    divClass(Jumbotron)(
      divClass(s"$Container hero-section")(
        h1("MusicPimp"),
        h2(subHeader),
        leadNormal(
          "Control your music libraries with your phone. Play the music on your phone, home stereo system, in your car or stream it anywhere.")
      )
    ),
    divClass("container page-content")(
      fullRow(
        h2(`class` := "centered", "Get it")
      ),
      row(
        div(`class` := "col-md-8 col-md-offset-2")(
          ol(`class` := "pimp-list get-it")(
            li(
              aHref(homeRoute.downloads, "Download"),
              " the free server for ",
              aHref(Home.msiDownload.url, "Windows"),
              " or ",
              aHref(Home.debDownload.url, "Linux"),
              "."
            ),
            li(
              "Get the ",
              strong("MusicPimp"),
              " apps for ",
              aHref(Home.iosAppUri, "iOS"),
              ", ",
              aHref(Home.androidAppUri, "Android"),
              " and ",
              aHref(Home.amazonAppUri, "Kindle Fire"),
              ", ",
              aHref(Home.winPhoneAppUri, "Windows Phone"),
              " and ",
              aHref(Home.winStoreAppUri, "Windows 8"),
              "."
            )
          )
        ),
      ),
      row(
        div4(
          a(href := Home.iosAppUri, `class` := s"d-none d-md-block $PullRight badge-ios")
        ),
        div4(
          badgeFromAssets(Home.androidAppUri, "Android app on Google Play", "badge-google"),
          badgeFromAssets(Home.amazonAppUri, "Android app on Amazon AppStore", "badge-amazon")
        ),
        div4(
          badgeFromAssets(Home.winPhoneAppUri, "Windows Phone app", "badge-winphone", PullLeft),
          badgeFromAssets(Home.winStoreAppUri, "Windows Store app", "badge-winstore", PullLeft)
        )
      ),
      hr(`class` := "hr-front"),
      row(
        divClass(s"${col.md.four} ${col.md.offset.two}")(
          h2("MusicBeamer"),
          leadNormal(
            "Stream tracks from your music library to any PC using ",
            strong("MusicBeamer"),
            " at ",
            a(href := "https://beam.musicpimp.org")("beam.musicpimp.org"),
            "."
          )
        ),
        div4(
          div(`class` := s"$PullLeft d-none d-md-block image-upload")
        )
      ),
      hr(`class` := "hr-front"),
      row(
        feature("PC to Phone", "pc-phone", "Make the music library on a PC available for playback on your phone."),
        feature("Phone to PC", "phone-pc", "Play the music stored on your phone on speakers connected to a PC."),
        feature("PC to Phone to PC",
                "pc-phone-pc",
                "Play music from your PC on another PC. Control playback with your phone.")
      ),
      hr(`class` := "hr-front"),
      row(
        divClass(s"${col.md.four} ${col.md.offset.two}")(
          h2("Getting Started"),
          leadNormal(
            "Get started in minutes. Check the ",
            a(href := homeRoute.docs)("documentation"),
            " for instructions."
          )
        ),
        divClass(col.md.four)(
          h2("Develop"),
          leadNormal("Build cool apps using the JSON ", a(href := DocsUrl)("API"), ".")
        )
      )
    )
  )

  val ping = index

  val linuxReqs: Modifier = Seq(
    h2("System Requirements"),
    ul(
      li("Java 8. Oracle Java is recommended."),
      li("A modern browser.")
    )
  )

  val debInstall: Modifier = Seq(
    headerRow("DEB"),
    fullRow(
      linuxReqs,
      h2("Installation"),
      p("Download ",
        aHref(Home.debDownload.url, Home.debDownload.fileName),
        " and execute: ",
        code(s"dpkg -i ${Home.debDownload.fileName}"))
    )
  )

  val debUninstall = p(code("apt-get purge musicpimp"))

  val docDeb = docuBase("deb", Home.linuxConfFile, debInstall, debUninstall)

  val winInstall: Modifier = Seq(
    headerRow("Windows"),
    fullRow(
      h2("System Requirements"),
      ul(
        li(aHref("http://java.com/en/download/index.jsp", "Java 8")),
        li("A modern browser, such as ",
           aHref("http://getfirefox.com", "Firefox"),
           " or ",
           aHref("http://www.google.com/chrome", "Chrome")),
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
          li(
            "Select tab ",
            strong("Manage"),
            " and specify folders containing MP3s under ",
            strong("Music Folders"),
            ":",
            p(img(src := images.usage_folders2_png, `class` := "img-responsive img-thumbnail"))
          ),
          li(
            "Open the ",
            strong("MusicPimp"),
            " app on your mobile device and add your PC as a music endpoint:",
            p(img(src := images.usage_wp8_png, `class` := "img-responsive img-thumbnail"))
          ),
          li("Enjoy your music.")
        ),
        h2("Supported Audio Formats"),
        p("MusicPimp supports MP3 playback."),
        h2("Connectivity"),
        p("Music is streamed over WLAN, mobile networks or Bluetooth."),
        h2("Advanced Configuration"),
        p(
          "Advanced settings can be configured in ",
          code("musicpimp.conf"),
          " in your installation directory. The path is typically ",
          code(confPath),
          ". Reasonable defaults are provided."
        ),
        h4("HTTPS"),
        p("To enable HTTPS, specify the following parameters in ", code("musicpimp.conf"), ":"),
        tag("table")(`class` := "table table-hover")(
          thead,
          tr(th("Key"), th("Value")),
          tbody(
            tr(td("https.port"), td("The HTTPS port to use")),
            tr(td("https.keyStore"), td("The path to the keystore")),
            tr(td("https.keyStorePassword"), td("The keystore password")),
            tr(td("https.keyStoreType"), td("Optionally, the keystore type. Defaults to JKS."))
          )
        ),
        p(
          "A keystore with a self-signed certificate is included in the MusicPimp software distributions in the folder ",
          code("config/security/test-cert.jks"),
          " under the installation directory. The keystore passphrase is ",
          strong("musicpimp"),
          "."
        ),
        p(
          spanClass("label label-info")("Note"),
          " In order to successfully connect to the MusicPimp (or Subsonic) server " +
            "over HTTPS using Windows Phone, your server certificate must be trusted by your phone. " +
            "This means self-signed certificates are unlikely to work."
        ),
        h2("Logging"),
        p(
          "MusicPimp writes a log to the directory given by the ",
          code("log.dir"),
          " system property. At midnight, the " +
            "log from the previous day is archived to a separate file and a new file is written for the following day. " +
            "The active log file is also archived if it reaches 100 MB in size (during the same day.) " +
            "Logs older than 30 days are finally deleted."
        ),
        h2("Uninstallation"),
        uninstallation
      )
    )

  def docPlain(os: String)(inner: Modifier*) = {
    def docLink(text: String, clicked: String, osId: String) = {
      val suffix = if (osId == os) " active" else ""
      button(`type` := "button", `class` := s"${btn.primary}$suffix", onclick := s"location.href='$clicked'")(text)
    }

    indexMain("documentation")(
      headerRow("Documentation", col.md.six),
      rowColumn(col.md.six)(
        div(`class` := s"${btn.group} last-box", attr("src-toggle") := "buttons-radio")(
          docLink("Windows", homeRoute.docs.uri, "win"),
          docLink("DEB", homeRoute.docsDeb.uri, "deb"),
        )
      ),
      rowColumn(col.md.eight)(inner)
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
      p(
        "You may wish to connect to your music server over HTTPS. HTTPS is " +
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
    headerRow("Privacy Policy", col.md.six),
    rowColumn(col.md.six)(
      p(PrivacyPolicy.text),
      p(PrivacyPolicy.purpose),
      p(PrivacyPolicy.roaming),
      p(PrivacyPolicy.network)
    )
  )

  val alarms = indexMain("alarms")(
    headerRow("Alarm clock management"),
    rowColumn(col.md.six)(
      leadNormal("Control the alarm clock on the MusicPimp server using the following API."),
      h4("Get all alarms ", small("GET /alarms")),
      p("Get an array of the currently configured alarms."),
      p("Example response:"),
      pre("""[
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
      p(
        "Add a new alarm or update an existing one. When updating, " +
          "include the ID of the alarm you update. If no ID is provided, " +
          "the interpretation is that you add a new alarm; the server will " +
          "generate a new ID."),
      pre("""{
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

  def downloads(releaseDate: String, previous: Seq[String]) =
    indexMain("downloads", Page(PageTitle, Seq("downloads")))(
      headerRow("Downloads"),
      fullRow(
        leadNormal("Download the server. It's free. No nonsense.")
      ),
      row(
        div(`class` := col.lg.four)(
          h2(iClass("icon-windows"), " Windows"),
          downloadLink(Home.msiDownload, s"primary ${btn.lg}"),
          p(s"Released on $releaseDate.")
        ),
        div(`class` := col.lg.four)(
          h2(iClass("icon-linux"), " Linux"),
          downloadLink(Home.debDownload, s"primary ${btn.lg}"),
          p("DEB packages are tested on Ubuntu."),
        )
      ),
      fullRow(
        h2("What next?"),
        p(
          "Install the software and navigate to ",
          aHref("http://localhost:8456/"),
          ". For more information, check the ",
          aHref(homeRoute.docs, "documentation"),
          "."
        )
      ),
      rowColumn(col.md.six)(
        h3("Previous versions"),
        ul(`class` := "pimp-list", id := FrontKeys.PimpListId)(
          previous map { prev =>
            liHref(Home.downloadBaseUrl + prev)(prev)
          }
        )
      )
    )

  val forum = indexMain("forum")(
    rowColumn(col.md.six)(
      divClass(PageHeader)(
        h1("Forum")
      )
    ),
    fullRow(
      "Visit this forum at ",
      a(href := "https://groups.google.com/forum/#!forum/musicpimp")("groups.google.com ", iconic("external-link")),
      "."
    )
  )

  val notFound = indexMain(PageTitle)(
    rowColumn(col.md.twelve)(
      divClass(s"$PageHeader text-center")(
        h1("Page not found")
      )
    ),
    row(
      divClass(s"${col.md.twelve} text-center")(
        "Return to the ",
        a(href := homeRoute.index)("frontpage"),
        "."
      )
    )
  )

  val about = indexMain("about")(
    headerRow("About"),
    row(
      divClass(col.md.six)(
        p("Developed by ", aHref("https://github.com/malliina", "Michael Skogberg"), "."),
        p(img(src := images.beauty_png, `class` := "img-responsive img-thumbnail")),
        p(
          "Should you have any questions, don't hesitate to:",
          ul(
            li("contact ", aHref("mailto:info@musicpimp.org", "info@musicpimp.org")),
            li("post in the ", a(href := homeRoute.forum)("forum ", iconic("comment-square"))),
            li("open an issue on ", aHref("https://github.com/malliina/musicpimp/issues", "GitHub"))
          )
        )
      ),
      divClass(col.md.six)(
        p("This site uses icons by ", aHref("https://useiconic.com/open", "Open Iconic"), "."),
        p("Developed with ", a(href := "https://www.jetbrains.com/idea/")("IntelliJ IDEA"), "."),
        p(
          a(href := "https://www.jetbrains.com/idea/")(
            img(src := images.logo_JetBrains_3_png, `class` := "img-responsive")))
      )
    )
  )

  def indexMain(tabName: String, pageConf: PageConf = Page.default)(inner: Modifier*) =
    indexNoContainer(tabName, pageConf)(
      divClass(s"$Container page-content")(inner)
    )

  def indexNoContainer(tabName: String, pageConf: PageConf = Page(PageTitle, Nil))(inner: Modifier*) = {
    def navItem[V: AttrValue](thisTabName: String, tabId: String, url: V, iconicName: String) = {
      val activeClass = if (tabId == tabName) " active" else ""
      li(`class` := s"nav-item$activeClass")(
        a(href := url, `class` := "nav-link")(iconic(iconicName), s" $thisTabName")
      )
    }

    plainMain(pageConf)(
      navbar.basic(
        homeRoute.index,
        "MusicPimp",
        modifier(
          ulClass(s"${navbars.Nav} $MrAuto mr-auto")(
            navItem("Home", "home", homeRoute.index, "home"),
            navItem("Downloads", "downloads", homeRoute.downloads, "data-transfer-download"),
            navItem("Documentation", "documentation", homeRoute.docs, "document"),
            navItem("Forum", "forum", homeRoute.forum, "comment-square")
          ),
          ulClass(s"${navbars.Nav} ${navbars.Right}")(
            navItem("Develop", "api", DocsUrl, "pencil"),
            navItem("About", "about", homeRoute.about, "globe")
          )
        )
      ),
      inner
    )
  }

  def plainMain(page: PageConf)(inner: Modifier*) = TagPage(
    html(lang := "en")(
      head(
        meta(charset := "utf-8"),
        titleTag(page.title),
        meta(name := "viewport", content := "width=device-width, initial-scale=1.0"),
        link(
          rel := "shortcut icon",
          href := "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAIAAAD9b0jDAAAABmJLR0QA/wD/AP+gvaeTAAAC5ElEQVRIib2V208TQRSHz5ntdVm2tA0gIASRWGOisRAf9c/xXxOJipcYteGmaDRGE+USQmkBpd4gQgrdnWEvMz6U7V6L9cV5Onv2N99+s53ZYu/tWehsxAevd5gkHeb+afw3qBAgREezOQfOO4Ky2gdW+9gJk20tsO3FcD8WbAhByyUQIjVUBCKdrdlYngLBkxduIfFxgqbs6zu7sWdr+2z3/dmadLNk1WvW0XdWCe4fP1TYtFxqlvrGC8GtdkTBzcbK3Wbd+HRH2GZbKN15Y+sHzZrTQ/blbVvNjWf28c9mbWt7tPw8Giq4STd9C6HlUkDBWY+prUx7O9rylLBOIqBsa4nTujfK2RHbfh2G6utPbO23t2PrB/rG0yBU2AatzIXn081ZYTGfpskCmo7stDCpD0qrL/lJIxzlRoNuL/k1H3JWj0iyur7+2IUKk9HKfDjnyM4JU3eeoWmrD9oltZV7TTMCALS60JoGACClQEq1roRJ9a1Xp5prM/zkuHULJRkl2bssff0RABBuaHRr0ftAVMdRHfd2WGWeGxpnR9rajC+ZvYrZaz7Z1fuc1SWi9Jv7ZY9mWsoXMdED+jfgzn7iNiJaxzXjx2eXGFOkvpuYzItGFbjhJC1AItmWAcJ2oz1XMJEFQIESsF+tvlWvWfUd8JwxKX8DU72ABDEu9F03eVglwjZa1xCTiTLcLEnXCMbc9yVsQ5ju3sKYgt0X3dcVV92kyXzHlKgF9zggolqANoPkJgBb3zBCsr5/GheKcQXlIe89lM9jTAkTMa6iMubrKGOYyERB1QIg+mcDZiJkSW4ilESSLQahGFdRHoiQkgcxofo6iSx2jUYklVFM5PzQTAEAw1EAQPWSTzMf0nSCJFd0oRjPYPpcJBEAMD2AiexpncyjPNw22TWCyV4HmrncLheQJbnJdgtyAkUAiGEyi+m+v0DTfZjMY6o7sD0ikvIQpvpjf9U8TWcKRIn4JaNkJ/8ArPM9PDd+ENcAAAAASUVORK5CYII="
        ),
        css.map { path =>
          cssLink(path)
        },
        js.map { path =>
          jsScript(path, defer)
        }
      ),
      body(`class` := page.bodyClasses.mkString(" "))(
        inner,
        footer(`class` := "footer")(
          divClass(Container)(
            spanClass("text-muted float-right")("Developed by ",
                                                a(href := "https://www.musicpimp.org")("Michael Skogberg"),
                                                ".")
          )
        )
      )
    )
  )

  def badgeFromAssets(link: String, altText: String, badgeClass: String, linkClass: String = "") =
    badge(link, altText, badgeClass, linkClass)

  def badge(link: String, altText: String, classes: String, linkClass: String) =
    a(href := link, `class` := s"d-none d-md-block badge $linkClass")(
      div(aria.labelledby := altText, `class` := s"badge-image $classes")
    )

  def feature(featureTitle: String, classes: String, leadText: String) =
    div4(
      h2(featureTitle),
      p(div(`class` := s"feature $classes")),
      leadNormal(leadText)
    )

  def leadNormal(content: Modifier*) = p(`class` := s"$Lead font-weight-normal")(content)

  def downloadLink(dl: Download, btnName: String = "primary") =
    p(
      a(`class` := s"${btn.Btn} ${btn.Btn}-$btnName", href := dl.url)(iconic("data-transfer-download"),
                                                                      s" ${dl.fileName}"))
}
