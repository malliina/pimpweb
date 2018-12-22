package controllers

import controllers.Assets.Asset
import play.api.mvc.{Action, AnyContent, Call}

object PimpAssets {
  def at(path: String): Call = routes.PimpAssets.versioned(path)
}

class PimpAssets(builder: AssetsBuilder) {
  def versioned(path: String, file: Asset): Action[AnyContent] = builder.versioned(path, file)
}
