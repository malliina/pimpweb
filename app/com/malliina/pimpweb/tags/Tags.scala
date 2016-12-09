package com.malliina.pimpweb.tags

import controllers.routes.Assets.at
import controllers.routes.Home
import play.api.mvc.Call

import scalatags.Text.GenericAttr
import scalatags.Text.all._

object Tags {
  implicit val callAttr = new GenericAttr[Call]
  val titleTag = tag("title")

  val about = indexMain("about")(
    div(`class` := "row")(
      div(`class` := "col-md-12")(
        div(`class` := "page-header")(
          h1("About")
        )
      )
    ),
    div(`class` := "row")(
      div(`class` := "col-md-6")(
        p("Developed by ", a(href := "https://mskogberg.info")("Michael Skogberg"), "."),
        p(img(src := at("img/handsome.png"), `class` := "img-responsive img-thumbnail")),
        p("Should you have any questions, don't hesitate to:",
          ul(
            li("contact ", a(href := "mailto:info@musicpimp.org")("info@musicpimp.org")),
            li("post in the ", a(href := Home.forum())("forum ", i(`class` := "glyphicon glyphicon-comment"))),
            li("open an issue on ", a(href := "https://github.com/malliina/musicpimp/issues")("GitHub"))
          ))
      ),
      div(`class` := "col-md-6")(
        p("This site uses icons by ", a(href := "http://glyphicons.com/")("Glyphicons"), " and ", a(href := "http://fontawesome.io/")("Font Awesome"), "."),
        p(a(href := "https://www.jetbrains.com/idea/")(img(src := at("img/logo_Jetbrains_3.png"), `class` := "img-responsive")))
      )
    )
  )

  def indexMain(tabName: String)(inner: Modifier*) = indexNoContainer(tabName)(
    div(`class` := "container")(inner)
  )

  def indexNoContainer(tabName: String)(inner: Modifier*) = {
    def navItem(thisTabName: String, tabId: String, url: Call, glyphicon: String) = {
      val maybeActive = if (tabId == tabName) Option(`class` := "active") else None
      li(maybeActive)(a(href := url)(i(`class` := s"glyphicon glyphicon-$glyphicon"), s" $thisTabName"))
    }

    plainMain("MusicPimp")(
      div(`class` := "navbar navbar-default")(
        div(`class` := "container")(
          div(`class` := "navbar-header")(
            button(`class` := "navbar-toggle", attr("data-toggle") := "collapse", attr("data-target") := ".navbar-collapse")(
              span(`class` := "icon-bar"),
              span(`class` := "icon-bar"),
              span(`class` := "icon-bar")
            ),
            a(`class` := "navbar-brand", href := Home.index())("MusicPimp")
          ),
          div(`class` := "navbar-collapse collapse")(
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
          div(`class` := "container")(
            p(`class` := "muted credit pull-right")("Developed by ", a(href := "https://mskogberg.info")("Michael Skogberg"))
          )
        )
      )
    )
  )

  def js[V: AttrValue](url: V) = script(src := url)

  def css[V: AttrValue](url: V) = link(rel := "stylesheet", href := url)
}
