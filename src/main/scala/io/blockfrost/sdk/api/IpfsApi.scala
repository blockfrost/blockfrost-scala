package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.IpfsApi.{IpfsObject, PinnedObject}
import io.blockfrost.sdk.common.{Config, PageRequest, SortedPageRequest, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.json4s.asJson
import sttp.client3.{Response, UriContext, asByteArray, multipartFile}

import java.io.{File, FileNotFoundException}

trait IpfsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def addObject(file: File)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[IpfsObject]]

  def getObject(path: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[Response[Either[String, Array[Byte]]]]

  def pinObject(path: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[PinnedObject]]

  def listPinnedObjects(pageRequest: PageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PinnedObject]]]

  def getPinnedObject(path: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[PinnedObject]]

  def removePinnedObject(path: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[PinnedObject]]
}

trait IpfsApiImpl[F[_], P] extends IpfsApi[F, P] {
  this: ApiClient[F, P] =>

  override def addObject(file: File)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[IpfsObject]] = {
    if (!file.exists()) throw new FileNotFoundException("File doesn't exist")
    basePost(uri"$host/ipfs/add")
      .multipartBody(multipartFile("file[0]", file))
      .response(asJson[IpfsObject])
      .send(sttpBackend)
  }

  override def getObject(path: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[Response[Either[String, Array[Byte]]]] =
    baseGet(uri"$host/ipfs/gateway/$path")
      .response(asByteArray)
      .send(sttpBackend)

  override def pinObject(path: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[PinnedObject]] =
    basePost(uri"$host/ipfs/pin/add/$path")
      .response(asJson[PinnedObject])
      .send(sttpBackend)

  override def listPinnedObjects(pageRequest: PageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PinnedObject]]] =
    get(uri"$host/ipfs/pin/list")

  override def getPinnedObject(path: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[PinnedObject]] =
    get(uri"$host/ipfs/pin/list/$path")

  override def removePinnedObject(path: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[PinnedObject]] =
    basePost(uri"$host/ipfs/pin/remove/$path")
      .response(asJson[PinnedObject])
      .send(sttpBackend)
}

object IpfsApi {
  case class IpfsObject(name: String, ipfs_hash: String, size: String)
  case class PinnedObject(ipfs_hash: String, state: String, size: Option[Double], time_created: Option[Double], time_pinned: Option[Double])
}
