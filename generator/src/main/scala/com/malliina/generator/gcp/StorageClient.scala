package com.malliina.generator.gcp

import java.io.FileInputStream
import java.nio.file.{Files, Path, Paths}

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.{BlobInfo, Storage, StorageOptions}
import com.malliina.generator.BucketName

import scala.jdk.CollectionConverters.SeqHasAsJava

object StorageClient {
  val credentialsFile = Paths.get(sys.props("user.home")).resolve(".gcp").resolve("credentials.json")

  def apply(): StorageClient =
    new StorageClient(StorageOptions.newBuilder().setCredentials(credentials).build().getService)

  def credentials = {
    val file = sys.env.get("GOOGLE_APPLICATION_CREDENTIALS").map(Paths.get(_)).getOrElse(credentialsFile)
    GoogleCredentials
      .fromStream(new FileInputStream(file.toFile))
      .createScoped(Seq("https://www.googleapis.com/auth/cloud-platform").asJava)
  }
}

class StorageClient(val client: Storage) {
  def bucket(name: BucketName) = client.get(name.value)

  def upload(blob: BlobInfo, file: Path) = client.create(blob, Files.readAllBytes(file))
}
