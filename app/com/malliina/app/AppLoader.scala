package com.malliina.app

import com.malliina.pimpweb.S3FileStore
import com.malliina.play.app.DefaultApp
import controllers.{Assets, Home}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import play.filters.gzip.GzipFilter
import router.Routes

class AppLoader extends DefaultApp(ctx => new AppComponents(ctx))

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  override lazy val httpFilters = Seq(new GzipFilter())
  lazy val assets = new Assets(httpErrorHandler)
  val home = new Home(S3FileStore)
  override val router: Router = new Routes(httpErrorHandler, home, assets)
}
