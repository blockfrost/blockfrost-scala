package io.blockfrost.sdk

import io.blockfrost.sdk.ApiClient.ApiKey
import sttp.client3.SttpBackend

trait ApiClient[F[_], P] {
  implicit val sttpBackend: SttpBackend[F, P]
  implicit val apiKey: ApiKey
  val host: String
}

object ApiClient {
  type ApiKey = String
}
