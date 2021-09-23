package io.blockfrost.sdk.util

import org.json4s.jackson.Serialization
import org.json4s.{Formats, Serialization}
import sttp.client3.{HttpError, Response, ResponseException}

import scala.concurrent.{ExecutionContext, Future}

object FutureResponseConverter {
  implicit class FutureResponseOps[A](response: Future[Response[Either[ResponseException[String, Exception], A]]])(implicit ec: ExecutionContext, formats: Formats, serialization: Serialization) {
    def extract: Future[A] = response.flatMap {
      _.body match {
        case Right(body) => Future.successful(body)
        case Left(error) => Future.failed(error)
      }
    } recoverWith {
      case HttpError(body: String, _) =>
        val error = Serialization.read[ApiError](body)
        Future.failed(ApiException(error))
    }
  }

  case class ApiError(status_code: Int, error: String, message: String)
  case class ApiException(error: ApiError) extends RuntimeException(error.message)
}
