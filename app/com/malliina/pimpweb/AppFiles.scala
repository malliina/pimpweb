package com.malliina.pimpweb

import java.nio.file.{Files, Path, Paths}

import com.malliina.util.Utils

import scala.io.Source

object AppFiles {
  val lineSep = sys.props("line.separator")
  val userDirString = sys.props("user.dir")
  var basePath = Paths get sys.props.getOrElse("app.home", userDirString)

  def readerFrom[T](resource: String)(code: Iterator[String] => T): T = {
    val maybeFile = pathTo(resource)
    if (Files exists maybeFile) {
      readerFrom(maybeFile)(code)
    } else {
      Utils.using(openStream(resource)) { inStream =>
        Utils.resource(Source.fromInputStream(inStream)) { source =>
          code(source.getLines())
        }
      }
    }
  }

  def openStream(resource: String) = openStreamOpt(resource).getOrElse(throw new Exception(s"Not found: '$resource'."))

  def openStreamOpt(resource: String) = Option(getClass.getClassLoader.getResourceAsStream(resource))

  def readerFrom[T](path: Path)(code: Iterator[String] => T): T =
    Utils.resource(Source.fromFile(path.toFile)) {
      source => code(source.getLines())
    }

  def pathTo(location: String) = basePath.resolve(location)
}
