package com.malliina.tags

import com.malliina.tags.Tags._

import scalatags.Text.all._

object Bootstrap extends Bootstrap

/**
  * Scalatags for Twitter Bootstrap.
  */
trait Bootstrap {
  val Btn = "btn"
  val BtnGroup = s"$Btn-group"
  val BtnPrimary = s"$Btn $Btn-primary"
  val Container = "container"
  val Jumbotron = "jumbotron"
  val Row = "row"
  val Collapse = "collapse"
  val ColMd3 = "col-md-3"
  val ColMd4 = "col-md-4"
  val ColMd6 = "col-md-6"
  val ColMd8 = "col-md-8"
  val ColMd9 = "col-md-9"
  val ColMd12 = "col-md-12"
  val ColMdOffset2 = "col-md-offset-2"
  val PageHeader = "page-header"
  val VisibleLg = "visible-lg"
  val VisibleMd = "visible-md"
  val VisibleSm = "visible-sm"
  val PullLeft = "pull-left"
  val PullRight = "pull-right"
  val Nav = "nav"
  val NavStacked = s"$Nav $Nav-stacked"
  val Navbar = "navbar"
  val NavbarBrand = "navbar-brand"
  val NavbarCollapse = "navbar-collapse"
  val NavbarHeader = "navbar-header"
  val NavbarDefault = "navbar-default"
  val NavbarNav = "navbar-nav"
  val NavbarRight = "navbar-right"
  val NavbarToggle = "navbar-toggle"

  def headerRow(header: String, clazz: String = ColMd12) =
    row(
      divClass(clazz)(
        divClass(PageHeader)(
          h1(header)
        )
      )
    )

  def fullRow(inner: Modifier*) = rowColumn(ColMd12)(inner)

  def rowColumn(clazz: String)(inner: Modifier*) = row(divClass(clazz)(inner))

  def row = divClass(Row)

  def div4 = divClass(ColMd4)

  def glyphIcon(glyphName: String) = iClass(s"glyphicon glyphicon-$glyphName")
}
