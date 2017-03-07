package com.malliina.app

import com.malliina.play.app.DefaultApp
import controllers.Assets
import controllers.Home
import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import router.Routes

class AppLoader extends DefaultApp(ctx => new AppComponents(ctx))

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  lazy val assets = new Assets(httpErrorHandler)
  override val router: Router = new Routes(httpErrorHandler, new Home, assets)
}
