package io.blockfrost.sdk

import sttp.client3.SttpBackend

trait ApiClient[F[_], P] {
  implicit val sttpBackend: SttpBackend[F, P]
  def apiKey: String
  def host: String
}
