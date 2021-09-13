package io.blockfrost.sdk.converter

import sttp.client3.{Response, ResponseException}

import scala.concurrent.{ExecutionContext, Future}

object FutureResponseConverter {
  implicit class FutureResponseOps[A](response: Future[Response[Either[ResponseException[String, Exception], A]]])(implicit ec: ExecutionContext) {
    def extract: Future[A] = response.flatMap {
      _.body match {
        case Right(body) => Future.successful(body)
        case Left(error) => Future.failed(error)
      }
    }
  }
}
