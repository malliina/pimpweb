import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.toPlatformDepsGroupID
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.{
  webpackConfigFile,
  webpackMonitoredDirectories,
  webpackMonitoredFiles
}

object GeneratorClientPlugin extends AutoPlugin {
  override def requires = ScalaJSBundlerPlugin

  override def projectSettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "com.lihaoyi" %%% "scalatags" % "0.9.3",
      "com.typesafe.play" %%% "play-json" % "2.9.2"
    ),
    scalaJSUseMainModuleInitializer := true,
    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack.dev.config.js"),
    webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack.prod.config.js"),
    // Enables hot-reload of CSS
    webpackMonitoredDirectories ++= (resourceDirectories in Compile).value.map { dir =>
      dir / "css"
    },
    includeFilter in webpackMonitoredFiles := "*.less",
    watchSources ++= (resourceDirectories in Compile).value.map { dir =>
      WatchSource(dir / "css", "*.less", HiddenFileFilter)
    }
  )
}
