package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.IpfsApi.{IpfsObject, PinnedObject}
import io.blockfrost.sdk.common.{PageRequest, SortedPageRequest, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.json4s.asJson
import sttp.client3.{Response, UriContext, asByteArray, basicRequest, multipartFile}

import java.io.{File, FileNotFoundException}

trait IpfsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def addObject(file: File)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[IpfsObject]]

  def getObject(path: String)(implicit formats: Formats, serialization: Serialization): F[Response[Either[String, Array[Byte]]]]

  def pinObject(path: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[PinnedObject]]

  def listPinnedObjects(pageRequest: PageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[PinnedObject]]]

  def getPinnedObject(path: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[PinnedObject]]

  def removePinnedObject(path: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[PinnedObject]]
}

trait IpfsApiImpl[F[_], P] extends IpfsApi[F, P] {
  this: ApiClient[F, P] =>

  override def addObject(file: File)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[IpfsObject]] = {
    if (!file.exists()) throw new FileNotFoundException("File doesn't exist")
    basicRequest.post(uri"$host/ipfs/add")
      .multipartBody(multipartFile("file[0]", file))
      .header("project_id", apiKey)
      .response(asJson[IpfsObject])
      .send(sttpBackend)
  }

  override def getObject(path: String)(implicit formats: Formats, serialization: Serialization): F[Response[Either[String, Array[Byte]]]] =
    basicRequest.get(uri"$host/ipfs/gateway/$path")
      .header("project_id", apiKey)
      .response(asByteArray)
      .send(sttpBackend)

  override def pinObject(path: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[PinnedObject]] =
    basicRequest.post(uri"$host/ipfs/pin/add/$path")
      .header("project_id", apiKey)
      .response(asJson[PinnedObject])
      .send(sttpBackend)

  override def listPinnedObjects(pageRequest: PageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[PinnedObject]]] =
    get(uri"$host/ipfs/pin/list")

  override def getPinnedObject(path: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[PinnedObject]] =
    get(uri"$host/ipfs/pin/list/$path")

  override def removePinnedObject(path: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[PinnedObject]] =
    basicRequest.post(uri"$host/ipfs/pin/remove/$path")
      .header("project_id", apiKey)
      .response(asJson[PinnedObject])
      .send(sttpBackend)
}

object IpfsApi {
  case class IpfsObject(name: String, ipfs_hash: String, size: String)
  case class PinnedObject(ipfs_hash: String, state: String, size: Option[Double], time_created: Option[Double], time_pinned: Option[Double])
}
