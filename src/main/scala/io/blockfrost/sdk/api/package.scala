package io.blockfrost.sdk

import sttp.client3.{Response, ResponseException}

package object api {
  type ApiResponse[R] = Response[Either[ResponseException[String, Exception], R]]
}
