package org.musicpimp.generator

import com.malliina.assets.FileAssets
import com.malliina.generator.{AssetFinder, AssetPath, HTML, TagPage}
import com.malliina.html.{Bootstrap, HtmlTags}
import com.malliina.pimpweb.Download
import org.musicpimp.generator.PimpWebHtml.{Page, PageConf, PageTitle, subHeader}
import scalatags.Text.all.*

object PimpWebHtml:
  val PageTitle = "MusicPimp"
  val subHeader = "No ads. No social media. Pure music."

  def apply(
    css: Seq[AssetPath],
    js: Seq[AssetPath],
    routes: Routes,
    paths: AssetFinder
  ): PimpWebHtml =
    new PimpWebHtml(css, js, routes, paths)

  trait PageConf:
    def title: String
    def bodyClasses: Seq[String]

  case class Page(title: String, bodyClasses: Seq[String]) extends PageConf

  object Page:
    def default = Page(PageTitle, Nil)

class PimpWebHtml(
  css: Seq[AssetPath],
  js: Seq[AssetPath],
  homeRoute: Routes,
  assetFinder: AssetFinder
) extends Bootstrap(HtmlTags)
  with HTML:

  import tags.*

  val DocsUrl = "https://www.musicpimp.org/docs/"

  val images = Images

  private def asset(path: AssetPath) = assetFinder.path(path)

  val index = indexNoContainer("home")(
    divClass("container-fluid py-5 old-jumbo")(
      divClass(s"$Container hero-section")(
        h1("MusicPimp"),
        h2(subHeader),
        leadNormal(
          "Control your music libraries with your phone. Play the music on your phone, home stereo system, in your car or stream it anywhere."
        )
      )
    ),
    divClass("container page-content")(
      fullRow(
        h2(cls := "centered", "Get it")
      ),
      row(
        div(cls := "col-md-8 col-md-offset-2")(
          ol(cls := "pimp-list get-it")(
            li(
              aHref(homeRoute.downloads, "Download"),
              " the free server for ",
              aHref(Urls.msiDownload.url, "Windows"),
              " or ",
              aHref(Urls.debDownload.url, "Linux"),
              "."
            ),
            li(
              "Get the ",
              strong("MusicPimp"),
              " apps for ",
              aHref(Urls.iosAppUri, "iOS"),
              ", ",
              aHref(Urls.androidAppUri, "Android"),
              " and ",
              aHref(Urls.amazonAppUri, "Kindle Fire"),
              ", ",
              aHref(Urls.winPhoneAppUri, "Windows Phone"),
              " and ",
              aHref(Urls.winStoreAppUri, "Windows 8"),
              "."
            )
          )
        )
      ),
      row(
        div4(
          a(href := Urls.iosAppUri, `class` := s"d-none d-md-block $PullRight app-badge-ios")
        ),
        div4(
          badgeFromAssets(Urls.androidAppUri, "Android app on Google Play", "app-badge-google"),
          badgeFromAssets(Urls.amazonAppUri, "Android app on Amazon AppStore", "app-badge-amazon")
        ),
        div4(
          badgeFromAssets(Urls.winPhoneAppUri, "Windows Phone app", "app-badge-winphone", PullLeft),
          badgeFromAssets(Urls.winStoreAppUri, "Windows Store app", "app-badge-winstore", PullLeft)
        )
      ),
      hr(`class` := "hr-front"),
      row(
        feature(
          "PC to Phone",
          "pc-phone",
          "Make the music library on a PC available for playback on your phone."
        ),
        feature(
          "Phone to PC",
          "phone-pc",
          "Play the music stored on your phone on speakers connected to a PC."
        ),
        feature(
          "PC to Phone to PC",
          "pc-phone-pc",
          "Play music from your PC on another PC. Control playback with your phone."
        )
      ),
      hr(`class` := "hr-front"),
      divClass(s"$Row mb-5")(
        divClass(s"${col.md.four} ${col.md.offset.two}")(
          h2("Getting Started"),
          leadNormal(
            "Get started in minutes. Check the ",
            aHref(homeRoute.docs, "documentation"),
            " for instructions."
          )
        ),
        divClass(col.md.four)(
          h2("Develop"),
          leadNormal("Build cool apps using the JSON ", aHref(DocsUrl, "API"), ".")
        )
      )
    )
  )

  val ping = index

  val linuxReqs: Modifier = Seq(
    docHeader("System Requirements"),
    ul(cls := "mb-5")(
      li("Java 11."),
      li("A modern browser.")
    )
  )

  val debInstall: Modifier = modifier(
    mainHeader("DEB"),
    rowColumn(s"${col.md.twelve}")(
      linuxReqs,
      docHeader("Installation"),
      p(cls := "mb-5")(
        "Download ",
        aHref(Urls.debDownload.url, Urls.debDownload.fileName),
        " and execute: ",
        code(s"dpkg -i ${Urls.debDownload.fileName}")
      )
    )
  )

  val debUninstall = p(cls := "mb-5")(code("apt-get purge musicpimp"))

  val docDeb = docuBase("deb", Urls.linuxConfFile, debInstall, debUninstall)

  val winInstall: Modifier = modifier(
    mainHeader("Windows"),
    rowColumn(s"${col.md.twelve}")(
      docHeader("System Requirements"),
      ul(cls := "mb-5")(
        li(aHref("https://java.com/en/download/index.jsp", "Java 11")),
        li(
          "A modern browser, such as ",
          aHref("https://getfirefox.com", "Firefox"),
          " or ",
          aHref("https://www.google.com/chrome", "Chrome")
        ),
        li(aHref("https://www.microsoft.com/net/download", ".NET Framework"), " 3.5 or higher")
      ),
      docHeader("Installation"),
      p(cls := "mb-5")(
        "Download and run ",
        aHref(Urls.msiDownload.url, Urls.msiDownload.fileName),
        "."
      )
    )
  )

  val winUninstall =
    p(cls := "mb-5")(
      "Uninstall MusicPimp using the Add/Remove Programs section in the Control Panel."
    )

  val docWin = docuBase("win", Urls.windowsConfFile, winInstall, winUninstall)

  def docuBase(
    os: String,
    confPath: String,
    installation: Modifier,
    uninstallation: Modifier
  ) =
    docPlain(os)(
      fullRow(
        installation,
        docHeader("Usage"),
        ol(cls := "mb-5")(
          li("Navigate to ", aHref("http://localhost:8456/"), " and login."),
          li(
            "Select tab ",
            strong("Manage"),
            " and specify folders containing MP3s under ",
            strong("Music Folders"),
            ":",
            img(
              src := asset(images.usage_folders2_png),
              cls := "img-responsive img-thumbnail my-4"
            )
          ),
          li(
            "Open the ",
            strong("MusicPimp"),
            " app on your mobile device and add your PC as a music endpoint:",
            img(src := asset(images.usage_wp8_png), cls := "img-responsive img-thumbnail my-4")
          ),
          li("Enjoy your music.")
        ),
        docHeader("Supported Audio Formats"),
        p(cls := "mb-5")("MusicPimp supports MP3 playback."),
        docHeader("Connectivity"),
        p(cls := "mb-5")("Music is streamed over WLAN, mobile networks or Bluetooth."),
        docHeader("Advanced Configuration"),
        p(cls := "mb-4")(
          "Advanced settings can be configured in ",
          code("musicpimp.conf"),
          " in your installation directory. The path is typically ",
          code(confPath),
          ". Reasonable defaults are provided."
        ),
        h4("HTTPS"),
        p("To enable HTTPS, specify the following parameters in ", code("musicpimp.conf"), ":"),
        tag("table")(cls := "table table-hover")(
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
        p(cls := "mb-5")(
          spanClass("badge badge-info")("Note"),
          " In order to successfully connect to the MusicPimp (or Subsonic) server " +
            "over HTTPS using Windows Phone, your server certificate must be trusted by your phone. " +
            "This means self-signed certificates are unlikely to work."
        ),
        docHeader("Logging"),
        p(cls := "mb-5")(
          "MusicPimp writes a log to the directory given by the ",
          code("log.dir"),
          " system property. At midnight, the " +
            "log from the previous day is archived to a separate file and a new file is written for the following day. " +
            "The active log file is also archived if it reaches 100 MB in size (during the same day.) " +
            "Logs older than 30 days are finally deleted."
        ),
        docHeader("Uninstallation"),
        uninstallation
      )
    )

  def docHeader(text: String) = h2(cls := "mb-4")(text)

  def docPlain(os: String)(inner: Modifier*) =
    def docLink(text: String, clicked: String, osId: String) =
      val suffix = if osId == os then " active" else ""
      button(
        `type` := "button",
        cls := s"${btn.primary}$suffix",
        onclick := s"location.href='$clicked'"
      )(text)

    indexMain("documentation")(
      mainHeader("Documentation"),
      rowColumn(col.md.six)(
        div(cls := s"${btn.group} last-box", attr("src-toggle") := "buttons-radio")(
          docLink("Windows", homeRoute.docs.uri, "win"),
          docLink("DEB", homeRoute.docsDeb.uri, "deb")
        )
      ),
      rowColumn(col.md.eight)(inner)
    )

  val docWinPhone = docPlain("wp")(
    mainHeader("Windows Phone"),
    fullRow(
      h2("System Requirements"),
      p("Windows Phone 7.5 and higher are supported."),
      h2("Installation"),
      p(
        "Install the ",
        strong("MusicPimp"),
        " ",
        aHref(Urls.winPhoneAppUri, "app"),
        " to your Windows Phone device."
      ),
      h2("Usage"),
      ol(
        li(
          "Add music endpoints to your app. Any PC on which MusicPimp is installed works as a music endpoint."
        ),
        li("Set the PC as the music source or use it for music playback in the app.")
      ),
      h3("HTTPS"),
      p(
        "You may wish to connect to your music server over HTTPS. HTTPS is " +
          "currently supported on Windows Phone provided that the server " +
          "certificate passes validation and is trusted by your phone. This " +
          "means that self-signed certificates will most likely not work. " +
          "Commercial certificates are likely to work."
      ),
      h3("Miscellaneous"),
      p(
        "MusicPimp for Windows Phone also supports the ",
        aHref("https://www.subsonic.org", "Subsonic"),
        " media streamer as a music server."
      )
    )
  )

  val privacyPolicy = indexMain("about")(
    mainHeader("Privacy Policy"),
    rowColumn(col.md.six)(
      p(PrivacyPolicy.text),
      p(PrivacyPolicy.purpose),
      p(PrivacyPolicy.roaming),
      p(PrivacyPolicy.network)
    )
  )

  val alarms = indexMain("alarms")(
    mainHeader("Alarm clock management"),
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
          "generate a new ID."
      ),
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
      mainHeader("Downloads"),
      fullRow(
        leadNormal("Download the server. It's free. No nonsense.")
      ),
      divClass(s"$Row mb-5 mt-4")(
        div(cls := col.lg.four)(
          h2(cls := "mb-4")(iClass("icon-windows"), " Windows"),
          downloadLink(Urls.msiDownload, s"primary ${btn.lg}"),
          p(s"Released on $releaseDate.")
        ),
        div(cls := col.lg.four)(
          h2(cls := "mb-4")(iClass("icon-linux"), " Linux"),
          downloadLink(Urls.debDownload, s"primary ${btn.lg}"),
          p("DEB packages are tested on Ubuntu.")
        )
      ),
      rowColumn(s"${col.md.twelve} mb-5")(
        h2("What next?"),
        p(
          "Install the software and navigate to ",
          aHref("http://localhost:8456/"),
          ". For more information, check the ",
          aHref(homeRoute.docs, "documentation"),
          "."
        )
      )
//      rowColumn(col.md.six)(
//        h3("Previous versions"),
//        ul(cls := "pimp-list", id := FrontKeys.PimpListId)(
//          previous map { prev =>
//            liHref(Urls.downloadBaseUrl + prev)(prev)
//          }
//        )
//      )
    )

  val forum = indexMain("forum")(
    rowColumn(col.md.six)(
      divClass(PageHeader)(
        h1(cls := "mb-5")("Forum")
      )
    ),
    fullRow(
      "Visit this forum at ",
      a(href := "https://groups.google.com/forum/#!forum/musicpimp")(
        "groups.google.com ",
        icon("up-right-from-square")
      ),
      "."
    )
  )

  val notFound = indexMain(PageTitle)(
    rowColumn(col.md.twelve)(
      divClass(s"$PageHeader text-center")(
        h1(`class` := "mb-5")("Page not found")
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
    mainHeader("About"),
    row(
      divClass(col.md.six)(
        p("Developed by ", aHref("https://github.com/malliina", "Michael Skogberg"), "."),
        p(img(src := asset(images.beauty_png), cls := "img-responsive img-thumbnail")),
        p(
          "Should you have any questions, don't hesitate to:",
          ul(
            li("contact ", aHref("mailto:info@musicpimp.org", "info@musicpimp.org")),
            li("post in the ", a(href := homeRoute.forum)("forum ", icon("message"))),
            li("open an issue on ", aHref("https://github.com/malliina/musicpimp/issues", "GitHub"))
          )
        )
      ),
      divClass(col.md.six)(
        p("Developed with ", a(href := "https://www.jetbrains.com/idea/")("IntelliJ IDEA"), "."),
        p(
          a(href := "https://www.jetbrains.com/idea/")(
            img(src := asset(images.logo_JetBrains_3_png), cls := "img-responsive")
          )
        )
      )
    )
  )

  def indexMain(tabName: String, pageConf: PageConf = Page.default)(inner: Modifier*) =
    indexNoContainer(tabName, pageConf)(
      divClass(s"$Container page-content")(inner)
    )

  def indexNoContainer(tabName: String, pageConf: PageConf = Page(PageTitle, Nil))(
    inner: Modifier*
  ) =
    def navItem[V: AttrValue](
      thisTabName: String,
      tabId: String,
      url: V,
      iconName: String
    ) =
      val activeClass = if tabId == tabName then " active" else ""
      li(`class` := s"nav-item$activeClass")(
        a(href := url, `class` := "nav-link")(icon(iconName), s" $thisTabName")
      )

    plainMain(pageConf)(
      navbar.basic(
        homeRoute.index,
        "MusicPimp",
        modifier(
          ulClass(s"${navbars.Nav} me-auto")(
            navItem("Home", "home", homeRoute.index, "home"),
            navItem("Downloads", "downloads", homeRoute.downloads, "download"),
            navItem("Documentation", "documentation", homeRoute.docs, "file"),
            navItem("Forum", "forum", homeRoute.forum, "message")
          ),
          ulClass(s"d-flex ${navbars.Nav} ${navbars.Right}")(
            navItem("Develop", "api", DocsUrl, "pencil"),
            navItem("About", "about", homeRoute.about, "globe")
          )
        )
      ),
      inner
    )

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
        cssLink(FileAssets.main_css),
        jsScript(FileAssets.main_js, defer)
      ),
      body(cls := page.bodyClasses.mkString(" "))(
        inner,
        footer(cls := "footer")(
          divClass(Container)(
            spanClass("text-muted float-end")(
              "Developed by ",
              aHref("https://www.musicpimp.org", "Michael Skogberg"),
              "."
            )
          )
        )
      )
    )
  )

  def badgeFromAssets(
    link: String,
    altText: String,
    badgeClass: String,
    linkClass: String = ""
  ) =
    badge(link, altText, badgeClass, linkClass)

  def badge(
    link: String,
    altText: String,
    classes: String,
    linkClass: String
  ) =
    a(href := link, cls := s"d-none d-md-block app-badge $linkClass")(
      div(aria.labelledby := altText, cls := s"app-badge-image $classes")
    )

  def feature(featureTitle: String, classes: String, leadText: String) =
    div4(
      h2(featureTitle),
      p(div(cls := s"feature $classes")),
      leadNormal(leadText)
    )

  def leadNormal(content: Modifier*) = p(cls := s"$Lead fw-normal")(content)

  def downloadLink(dl: Download, btnName: String = "primary") =
    p(
      a(`class` := s"${btn.Btn} ${btn.Btn}-$btnName", href := dl.url)(
        icon("download"),
        s" ${dl.fileName}"
      )
    )

  def icon(iconName: String) =
    i(cls := s"nav-icon $iconName", title := iconName, aria.hidden := True)

  def mainHeader(text: String, width: String = col.md.twelve) =
    divClass(Row)(
      divClass(width)(
        h1(`class` := "mb-5 mt-4")(text)
      )
    )
