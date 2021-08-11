package io.blockfrost.sdk

import io.blockfrost.sdk.common.Testnet
import org.json4s.Formats
import org.scalatest.Assertion
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

trait TestContextSupport {
  implicit val formats: Formats = io.blockfrost.sdk.common.Serialization.formats
  implicit val serialization: org.json4s.Serialization = org.json4s.jackson.Serialization
  lazy val asyncClient: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

  def genericTestContext[A](test: A => Future[Assertion])(implicit context: A): Future[Assertion] = test(context)

  trait TestApiClient extends ApiClient[Future, Any] {
    override implicit val sttpBackend: SttpBackend[Future, Any] = asyncClient

    override def apiKey: String = sys.env.getOrElse("BLOCKFROST_API_KEY", throw new RuntimeException("Api key not found in environment variables"))

    override def host: String = Testnet.url
  }
}
