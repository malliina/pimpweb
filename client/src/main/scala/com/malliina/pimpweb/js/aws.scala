//package com.malliina.pimpweb.js
//
//import scala.scalajs.js
//import scala.scalajs.js.Dynamic.literal
//import scala.scalajs.js.annotation.JSImport
//
//@js.native
//@JSImport("aws-sdk/clients/s3", JSImport.Default)
//class AwsS3(options: S3Options) extends js.Object {
//  def listObjects(options: ListObjectsOptions, callback: js.Function2[js.Any, js.Any, Unit]): Unit = js.native
//}
//
//@js.native
//trait S3Options extends js.Object {
//  def apiVersion: String = js.native // '2006-03-01',
//  def region: String = js.native // 'us-west-1',
//  //  def credentials: js.Any = js.native
//}
//
//object S3Options {
//  def apply(apiVersion: String, region: String) =
//    literal(apiVersion = apiVersion, region = region).asInstanceOf[S3Options]
//
//  def region(reg: String) = apply("2006-03-01", reg)
//}
//
//@js.native
//trait ListObjectsOptions extends js.Object {
//  def Bucket: String = js.native
//
//  def Prefix: String = js.native
//}
//
//object ListObjectsOptions {
//  def apply(bucket: String, prefix: String): ListObjectsOptions =
//    literal(Bucket = bucket, Prefix = prefix).asInstanceOf[ListObjectsOptions]
//}
