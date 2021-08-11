package io.blockfrost.sdk.common

import io.blockfrost.sdk.ApiClient.ApiKey
import org.json4s.{Formats, Serialization}
import sttp.client3.json4s.asJson
import sttp.client3.{Response, ResponseException, SttpBackend, basicRequest}
import sttp.model.Uri

trait SttpSupport {
  type ApiResponse[R] = Response[Either[ResponseException[String, Exception], R]]

  def get[F[_], P, R: Manifest](uri: Uri)(implicit apiKey: ApiKey, formats: Formats, serialization: Serialization, sttpBackend: SttpBackend[F, P]): F[ApiResponse[R]] =
    basicRequest.get(uri)
      .header("project_id", apiKey)
      .response(asJson[R])
      .send(sttpBackend)
}
