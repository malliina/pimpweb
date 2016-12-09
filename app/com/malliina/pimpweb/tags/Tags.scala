package com.malliina.pimpweb.tags

import com.malliina.file.FileUtilities
import controllers.Docs
import controllers.routes.Assets.at
import controllers.routes.Home
import play.api.mvc.Call

import scalatags.Text.GenericAttr
import scalatags.Text.all._

object Tags {
  implicit val callAttr = new GenericAttr[Call]
  val titleTag = tag("title")

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
          pClass("lead")("Develop apps for MusicPimp using the JSON API.")
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
        p("Developed by ", a(href := "https://mskogberg.info")("Michael Skogberg"), "."),
        p(img(src := at("img/handsome.png"), `class` := "img-responsive img-thumbnail")),
        p("Should you have any questions, don't hesitate to:",
          ul(
            li("contact ", a(href := "mailto:info@musicpimp.org")("info@musicpimp.org")),
            li("post in the ", a(href := Home.forum())("forum ", i(`class` := "glyphicon glyphicon-comment"))),
            li("open an issue on ", a(href := "https://github.com/malliina/musicpimp/issues")("GitHub"))
          )
        )
      ),
      divClass("col-md-6")(
        p("This site uses icons by ", a(href := "http://glyphicons.com/")("Glyphicons"), " and ", a(href := "http://fontawesome.io/")("Font Awesome"), "."),
        p(a(href := "https://www.jetbrains.com/idea/")(img(src := at("img/logo_Jetbrains_3.png"), `class` := "img-responsive")))
      )
    )
  )

  def indexMain(tabName: String)(inner: Modifier*) = indexNoContainer(tabName)(
    divClass("container")(inner)
  )

  def indexNoContainer(tabName: String)(inner: Modifier*) = {
    def navItem(thisTabName: String, tabId: String, url: Call, glyphicon: String) = {
      val maybeActive = if (tabId == tabName) Option(`class` := "active") else None
      li(maybeActive)(a(href := url)(i(`class` := s"glyphicon glyphicon-$glyphicon"), s" $thisTabName"))
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
            a(`class` := "navbar-brand", href := Home.index())("MusicPimp")
          ),
          divClass("navbar-collapse collapse")(
            ul(`class` := "nav navbar-nav")(
              navItem("Home", "home", Home.index(), "home"),
              navItem("Downloads", "downloads", Home.downloads(), "download-alt"),
              navItem("Documentation", "documentation", Home.win(), "list-alt"),
              navItem("Forum", "forum", Home.forum(), "comment")
            ),
            ul(`class` := "nav navbar-nav navbar-right")(
              navItem("Develop", "api", Home.api(), "edit"),
              navItem("About", "about", Home.about(), "globe")
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
            pClass("muted credit pull-right")("Developed by ", a(href := "https://mskogberg.info")("Michael Skogberg"))
          )
        )
      )
    )
  )

  def headerRow(header: String) =
    row(
      divClass("col-md-12")(
        divClass("page-header")(
          h1(header)
        )
      )
    )

  def row = divClass("row")

  def divClass(clazz: String) = div(`class` := clazz)

  def spanClass(clazz: String) = span(`class` := "clazz")

  def pClass(clazz: String) = p(`class` := clazz)

  def liHref(url: String, text: String) = li(aHref(url, text))

  def aHref(url: String, text: String) = a(href := url)(text)

  def js[V: AttrValue](url: V) = script(src := url)

  def css[V: AttrValue](url: V) = link(rel := "stylesheet", href := url)

  def markdown(docName: String): Modifier = Docs.toHtml(markdownAsString(docName)).map(RawFrag.apply)

  def markdownAsString(docName: String) =
    FileUtilities.readerFrom(s"docs/$docName.md")(_.mkString(FileUtilities.lineSep))
}
