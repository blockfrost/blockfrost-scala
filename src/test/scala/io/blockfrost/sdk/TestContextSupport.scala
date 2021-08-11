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

  def genericTestContext[A](test: A => Future[Assertion])(implicit context: A): Future[Assertion] = test(context)

  trait TestApiClient extends ApiClient[Future, Any] {
    override implicit val sttpBackend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

    override implicit val apiKey: String = sys.env.getOrElse("BLOCKFROST_API_KEY", "Ne3uGRoN1t8t0ApCIAf6XEFnb43ljKOE")

    override val host: String = Testnet.url
  }
}
