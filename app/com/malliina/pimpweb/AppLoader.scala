package com.malliina.pimpweb

import com.malliina.play.app.DefaultApp
import controllers.{AssetsComponents, Home, PimpAssets}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.filters.gzip.GzipFilter
import router.Routes

class AppLoader extends DefaultApp(ctx => new AppComponents(ctx))

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with HttpFiltersComponents
  with AssetsComponents {

  override lazy val httpFilters = Seq(new GzipFilter())
  val pimpAssets = new PimpAssets(assets)
  val home = Home.s3(controllerComponents)
  override val router: Router = new Routes(httpErrorHandler, home, pimpAssets)
}
