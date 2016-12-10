package com.malliina.pimpweb.tags

import com.malliina.file.FileUtilities
import controllers.routes.Assets.at
import controllers.routes.{Home => HomeRoute}
import controllers.{Docs, Home}
import play.api.mvc.Call

import scalatags.Text.{GenericAttr, TypedTag}
import scalatags.Text.all._

object Tags {
  implicit val callAttr = new GenericAttr[Call]
  val titleTag = tag("title")

  def downloads(previous: Seq[String]) = indexMain("downloads")(
    headerRow("Downloads"),
    fullRow(
      leadPara("Download the server. It's free. No nonsense.")
    ),
    row(
      div4(
        h2(iClass("icon-windows"), " Windows"),
        leadPara("Download for Windows"),
        downloadLink(Home.msiDownload, "primary btn-lg"),
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
    rowColumn("col-md-6")(
      h3("Previous versions"),
      ul(
        previous map { prev =>
          liHref(Home.downloadBaseUrl + prev, prev)
        }
      )
    )
  )

  val index = indexNoContainer("home")(
    divClass("jumbotron")(
      divClass("container")(
        h1("MusicPimp", small(`class` := "pull-right")("No ads. No social media. Pure music.")),
        leadPara("Control your music libraries with your phone. Play the music on your phone, home stereo system, in your car or stream it anywhere.")
      )
    ),
    divClass("container")(
      fullRow(
        h2("Get it"),
        a(`class` := "btn btn-primary centered", href := Home.msiDownload.url)(i(`class` := "glyphicon glyphicon-download"), " Download for Windows"),
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
          a(href := Home.iosAppUri, `class` := "visible-lg visible-md pull-right badge-ios")
        ),
        div4(
          badgeFromAssets(Home.androidAppUri, "Android app on Google Play", "en_app_rgb_wo_60.png"),
          badgeFromAssets(Home.amazonAppUri, "Android app on Amazon AppStore", "amazon-apps-kindle-us-gray.png")
        ),
        div4(
          badgeFromAssets(Home.winPhoneAppUri, "Windows Phone app", "badge_winphone2.png", "pull-left"),
          badgeFromAssets(Home.winStoreAppUri, "Windows Store app", "badge_winstore.png", "pull-left")
        )
      ),
      hr,
      row(
        divClass("col-md-4 col-md-offset-2")(
          h2("MusicBeamer"),
          leadPara(
            "Stream tracks from your music library to any PC using ",
            strong("MusicBeamer"), " at ", aHref("https://beam.musicpimp.org"), "."
          )
        ),
        div4(
          img(src := at("img/upload-alt-blue-128.png"), `class` := "pull-left visible-lg visible-md")
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
        divClass("col-md-4 col-md-offset-2")(
          h2("Getting Started"),
          leadPara(
            "Get started in minutes. Check the ",
            aHref(HomeRoute.win(), "documentation"),
            " for instructions."
          )
        ),
        divClass("col-md-4")(
          h2("Develop"),
          leadPara("Build cool apps using the JSON ", aHref(HomeRoute.api(), "API"), ".")
        )
      )
    )
  )

  def badgeFromAssets(link: String, altText: String, file: String, classes: String = "") =
    badge(link, altText, at(s"img/$file").toString, classes)

  def badge(link: String, altText: String, imgUrl: String, classes: String) =
    a(href := link, `class` := s"visible-lg visible-md badge $classes")(img(alt := altText, src := imgUrl, `class` := "badge-image"))

  def feature(featureTitle: String, imgFile: String, leadText: String) =
    div4(
      h2(featureTitle),
      p(img(src := at(s"img/$imgFile"))),
      leadPara(leadText)
    )

  val forum = indexMain("forum")(
    rowColumn("col-md-6")(
      divClass("page-header")(
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
        divClass("col-md-9")(
          leadPara("Develop apps for MusicPimp using the JSON API.")
        )
      ),
      row(
        divClass("col-md-8")(
          requests,
          responses,
          httpEndpoints,
          serverEvents
        ),
        tag("nav")(`class` := "col-md-3 bs-docs-sidebar", id := "sidenav")(
          ul(`class` := "nav nav-stacked affix", id := "sidebar")(
            liHref("#requests", "Requests"),
            liHref("#responses", "Responses"),
            li(
              aHref("#endpoints", "HTTP endpoints"),
              ul(`class` := "nav nav-stacked")(
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
      divClass("col-md-6")(
        p("Developed by ", aHref("https://mskogberg.info", "Michael Skogberg"), "."),
        p(img(src := at("img/handsome.png"), `class` := "img-responsive img-thumbnail")),
        p("Should you have any questions, don't hesitate to:",
          ul(
            li("contact ", aHref("mailto:info@musicpimp.org", "info@musicpimp.org")),
            li("post in the ", a(href := HomeRoute.forum())("forum ", i(`class` := "glyphicon glyphicon-comment"))),
            li("open an issue on ", aHref("https://github.com/malliina/musicpimp/issues", "GitHub"))
          )
        )
      ),
      divClass("col-md-6")(
        p("This site uses icons by ", aHref("http://glyphicons.com/", "Glyphicons"), " and ", aHref("http://fontawesome.io/", "Font Awesome"), "."),
        p(a(href := "https://www.jetbrains.com/idea/")(img(src := at("img/logo_Jetbrains_3.png"), `class` := "img-responsive")))
      )
    )
  )

  def indexMain(tabName: String)(inner: Modifier*) = indexNoContainer(tabName)(
    divClass("container")(inner)
  )

  def indexNoContainer(tabName: String)(inner: Modifier*) = {
    def navItem(thisTabName: String, tabId: String, url: Call, glyphiconName: String) = {
      val maybeActive = if (tabId == tabName) Option(`class` := "active") else None
      li(maybeActive)(a(href := url)(glyphIcon(glyphiconName), s" $thisTabName"))
    }

    plainMain("MusicPimp")(
      divClass("navbar navbar-default")(
        divClass("container")(
          divClass("navbar-header")(
            button(`class` := "navbar-toggle", attr("data-toggle") := "collapse", attr("data-target") := ".navbar-collapse")(
              spanClass("icon-bar"),
              spanClass("icon-bar"),
              spanClass("icon-bar")
            ),
            a(`class` := "navbar-brand", href := HomeRoute.index())("MusicPimp")
          ),
          divClass("navbar-collapse collapse")(
            ul(`class` := "nav navbar-nav")(
              navItem("Home", "home", HomeRoute.index(), "home"),
              navItem("Downloads", "downloads", HomeRoute.downloads(), "download-alt"),
              navItem("Documentation", "documentation", HomeRoute.win(), "list-alt"),
              navItem("Forum", "forum", HomeRoute.forum(), "comment")
            ),
            ul(`class` := "nav navbar-nav navbar-right")(
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
          divClass("container")(
            pClass("muted credit pull-right")("Developed by ", a(href := "https://mskogberg.info")("Michael Skogberg"), ".")
          )
        )
      )
    )
  )

  def downloadLink(dl: Home.Download, btnName: String = "default") =
    p(a(`class` := s"btn btn-$btnName", href := dl.url)(glyphIcon("download"), s" ${dl.fileName}"))

  def headerRow(header: String) =
    row(
      divClass("col-md-12")(
        divClass("page-header")(
          h1(header)
        )
      )
    )

  def fullRow(inner: Modifier*) = rowColumn("col-md-12")(inner)

  def rowColumn(clazz: String)(inner: Modifier*) = row(div(`class` := clazz)(inner))

  def row = divClass("row")

  def div4 = divClass("col-md-4")

  def divClass(clazz: String) = div(`class` := clazz)

  def spanClass(clazz: String) = span(`class` := clazz)

  def glyphIcon(glyphName: String) = iClass(s"glyphicon glyphicon-$glyphName")

  def iClass(clazz: String) = i(`class` := clazz)

  def leadPara = pClass("lead")

  def pClass(clazz: String) = p(`class` := clazz)

  def liHref(url: String, text: String) = li(aHref(url, text))

  def aHref(url: String): TypedTag[String] = aHref(url, url)

  def aHref[V: AttrValue](url: V, text: String): TypedTag[String] = a(href := url)(text)

  def js[V: AttrValue](url: V) = script(src := url)

  def css[V: AttrValue](url: V) = link(rel := "stylesheet", href := url)

  def markdown(docName: String): Modifier = Docs.toHtml(markdownAsString(docName)).map(RawFrag.apply)

  def markdownAsString(docName: String) =
    FileUtilities.readerFrom(s"docs/$docName.md")(_.mkString(FileUtilities.lineSep))
}
