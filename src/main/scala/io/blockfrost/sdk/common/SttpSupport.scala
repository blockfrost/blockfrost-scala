package io.blockfrost.sdk.common

import io.blockfrost.sdk.ApiClient.ApiKey
import io.blockfrost.sdk.api.ApiResponse
import org.json4s.{Formats, Serialization}
import sttp.client3.json4s.asJson
import sttp.client3.{Identity, RequestT, SttpBackend, basicRequest}
import sttp.model.Uri.QuerySegment.KeyValue
import sttp.model.{MediaType, Uri}

import scala.concurrent.duration.DurationInt

trait SttpSupport {
  private def sdkVersion = "0.1.1"

  def get[F[_], P, R: Manifest](uri: Uri, pageRequest: Option[PageRequest] = None)
                               (implicit key: ApiKey, f: Formats, s: Serialization, b: SttpBackend[F, P], config: Config): F[ApiResponse[R]] = {
    val uriWithQueryParams = pageRequest.map {
      case SortedPageRequest(count, page, order) => uri.addQuerySegment(KeyValue("page", page.toString)).addQuerySegment(KeyValue("count", count.toString)).addQuerySegment(KeyValue("order", order.get))
      case UnsortedPageRequest(count, page) => uri.addQuerySegment(KeyValue("page", page.toString)).addQuerySegment(KeyValue("count", count.toString))
    }.getOrElse(uri)
    baseGet(uriWithQueryParams)
      .response(asJson[R])
      .send(b)
  }

  def post[F[_], P, R: Manifest](uri: Uri, body: Array[Byte], contentType: String)
                                (implicit key: ApiKey, f: Formats, s: Serialization, b: SttpBackend[F, P], config: Config): F[ApiResponse[R]] = {
    basePost(uri)
      .contentType(MediaType.unsafeParse(contentType))
      .body(body)
      .response(asJson[R])
      .send(b)
  }

  def baseGet(uri: Uri)(implicit key: ApiKey, config: Config): RequestT[Identity, Either[String, String], Any] = {
    basicRequest.get(uri)
      .header("User-Agent", s"blockfrost-scala-$sdkVersion")
      .header("project_id", key)
      .readTimeout(config.readTimeoutMillis.millis)
  }

  def basePost(uri: Uri)(implicit key: ApiKey, config: Config): RequestT[Identity, Either[String, String], Any] = {
    basicRequest.post(uri)
      .header("User-Agent", s"blockfrost-scala-$sdkVersion")
      .header("project_id", key)
      .readTimeout(config.readTimeoutMillis.millis)
  }
}
