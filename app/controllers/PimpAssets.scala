package controllers

import controllers.Assets.Asset

object PimpAssets {
  def at(path: String) = routes.PimpAssets.versioned(path)
}

class PimpAssets(builder: AssetsBuilder) {
  def versioned(path: String, file: Asset) = builder.versioned(path, file)
}
