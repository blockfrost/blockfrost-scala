package io.blockfrost.sdk.common

import io.blockfrost.sdk.ApiClient.ApiKey
import org.json4s.{Formats, Serialization}
import sttp.client3.json4s.asJson
import sttp.client3.{Response, ResponseException, SttpBackend, basicRequest}
import sttp.model.Uri
import sttp.model.Uri.QuerySegment.KeyValue

trait SttpSupport {
  type ApiResponse[R] = Response[Either[ResponseException[String, Exception], R]]

  def get[F[_], P, R: Manifest](uri: Uri, pageRequest: Option[PageRequest] = None)
                               (implicit key: ApiKey, f: Formats, s: Serialization, b: SttpBackend[F, P]): F[ApiResponse[R]] = {
    val uriWithQueryParams = pageRequest.map {
      case SortedPageRequest(count, page, order) => uri.addQuerySegment(KeyValue("page", page.toString)).addQuerySegment(KeyValue("count", count.toString)).addQuerySegment(KeyValue("order", order))
      case UnsortedPageRequest(count, page) => uri.addQuerySegment(KeyValue("page", page.toString)).addQuerySegment(KeyValue("count", count.toString))
    }.getOrElse(uri)
    basicRequest.get(uriWithQueryParams)
      .header("project_id", key)
      .response(asJson[R])
      .send(b)
  }
}
