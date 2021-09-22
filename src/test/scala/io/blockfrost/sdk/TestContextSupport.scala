package io.blockfrost.sdk

import io.blockfrost.sdk.common.{Config, IPFS, Mainnet, Testnet}
import org.json4s.Formats
import org.scalatest.Assertion
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future
import scala.language.implicitConversions

trait TestContextSupport {
  implicit val formats: Formats = org.json4s.DefaultFormats
  implicit val serialization: org.json4s.Serialization = org.json4s.jackson.Serialization
  implicit val sdkConfig: Config = Config(5, 500, 5000)
  val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

  def genericTestContext[A](test: A => Future[Assertion])(implicit context: A): Future[Assertion] = test(context)

  trait MainnetApiClient extends ApiClient[Future, Any] {
    override implicit val sttpBackend: SttpBackend[Future, Any] = backend

    override implicit val apiKey: String = sys.env.getOrElse("BF_MAINNET_PROJECT_ID", throw new RuntimeException("Api key not found in environment variables"))

    override val host: String = Mainnet.url
  }

  trait TestnetApiClient extends ApiClient[Future, Any] {
    override implicit val sttpBackend: SttpBackend[Future, Any] = backend

    override implicit val apiKey: String = sys.env.getOrElse("BLOCKFROST_API_KEY", throw new RuntimeException("Api key not found in environment variables"))

    override val host: String = Testnet.url
  }

  trait TestIpfsApiClient extends ApiClient[Future, Any] {
    override implicit val sttpBackend: SttpBackend[Future, Any] = backend

    override implicit val apiKey: String = sys.env.getOrElse("BLOCKFROST_IPFS_API_KEY", throw new RuntimeException("Api key not found in environment variables"))

    override val host: String = IPFS.url
  }
}

object TestContextSupport {
  implicit class RichInt(n: Int) {
    def some: Option[Int] = Some(n)
  }

  implicit class RichString(s: String) {
    def some: Option[String] = Some(s)
  }
}
