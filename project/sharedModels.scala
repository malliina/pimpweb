import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor, StandardOpenOption}

import com.malliina.values.{StringCompanion, StringEnumCompanion, WrappedString}
import play.api.libs.json._
import sbt.File

import scala.sys.process.ProcessLogger

case class BucketName(value: String) extends AnyVal with WrappedString
object BucketName extends StringCompanion[BucketName]

sealed abstract class Command(val name: String)

object Command extends StringEnumCompanion[Command] {
  override def all = Seq(Build, Deploy)
  override def write(t: Command) = t.name

  case object Build extends Command("build")
  case object Deploy extends Command("deploy")
}

sealed abstract class DeployTarget(val name: String)

object DeployTarget {
  val ServiceKey = "service"

  implicit val json: Format[DeployTarget] = Format[DeployTarget](
    json =>
      (json \ ServiceKey).validate[String].flatMap {
        case NetlifyTarget.name => JsSuccess(NetlifyTarget)
        case GCP.name           => GCP.gcpJson.reads(json)
        case GitHubTarget.name  => GitHubTarget.ghJson.reads(json)
        case other              => JsError(s"Unknown service: '$other'.")
      }, {
      case NetlifyTarget =>
        Json.obj(ServiceKey -> NetlifyTarget.name)
      case gcp @ GCPTarget(_) =>
        Json.obj(ServiceKey -> GCP.name) ++ GCP.gcpJson.writes(gcp)
      case gh @ GitHubTarget(_) =>
        Json.obj(ServiceKey -> GitHubTarget.name) ++ GitHubTarget.ghJson.writes(gh)
    }
  )
  case class GCPTarget(bucket: BucketName) extends DeployTarget(GCP.name)
  object GCP {
    val name = "gcp"
    val gcpJson = Json.format[GCPTarget]
  }
  case object NetlifyTarget extends DeployTarget("netlify")
  case class GitHubTarget(cname: String) extends DeployTarget(GitHubTarget.name)
  object GitHubTarget {
    val name = "github"
    val ghJson = Json.format[GitHubTarget]
  }
}

case class BuildSpec(cmd: Command, manifest: Path, target: DeployTarget)

object BuildSpec {
  implicit val paths = Formats.pathFormat
  implicit val json = Json.format[BuildSpec]
}

/**
  *
  * @param assetsBase typically .../scalajs-bundler/main
  */
case class AssetsManifest(
    scripts: Seq[Path],
    adhocScripts: Seq[String],
    styles: Seq[Path],
    statics: Seq[Path],
    assetsBase: Path
) {
  def to(file: Path, log: ProcessLogger) = {
    FileIO.writeJson(this, file, log)(AssetsManifest.json)
  }
}

object AssetsManifest {
  implicit val paths = Formats.pathFormat
  implicit val json = Json.format[AssetsManifest]
}

case class AssetGroup(scripts: Seq[File], adhocScripts: Seq[String], styles: Seq[File], statics: Seq[File]) {
  def manifest(assetsBase: File): AssetsManifest =
    AssetsManifest(scripts.map(_.toPath), adhocScripts, styles.map(_.toPath), statics.map(_.toPath), assetsBase.toPath)
}

object FileIO extends FileIO

class FileIO {
  def writeJson[T: Writes](t: T, to: Path, log: ProcessLogger): Path =
    write(Json.prettyPrint(Json.toJson(t)).getBytes(StandardCharsets.UTF_8), to, log)

  def write(bytes: Array[Byte], to: Path, log: ProcessLogger): Path = {
    if (!Files.isRegularFile(to)) {
      val dir = to.getParent
      if (!Files.isDirectory(dir))
        Files.createDirectories(dir)
      Files.createFile(to)
    }
    Files.write(to, bytes, StandardOpenOption.TRUNCATE_EXISTING)
    log.out(s"Wrote ${to.toAbsolutePath}.")
    to
  }

  // https://stackoverflow.com/a/27917071
  def deleteDirectory(dir: Path): Path =
    if (Files.exists(dir)) {
      Files.walkFileTree(
        dir,
        new SimpleFileVisitor[Path] {
          override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
            Files.delete(file)
            FileVisitResult.CONTINUE
          }

          override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
            Files.delete(dir)
            FileVisitResult.CONTINUE
          }
        }
      )
    } else {
      dir
    }
}
