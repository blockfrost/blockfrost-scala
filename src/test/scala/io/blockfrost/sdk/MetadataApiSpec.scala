package io.blockfrost.sdk

import io.blockfrost.sdk.api.MetadataApi.{MetadataContentCbor, MetadataContentJson, MetadataLabel}
import io.blockfrost.sdk.api.{MetadataApi, MetadataApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.converter.FutureResponseConverter.FutureResponseOps
import org.json4s.JString
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class MetadataApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {

  "getTransactionMetadataLabels" should "return sequence of MetadataLabel" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getTransactionMetadataLabels(SortedPageRequest(1))
      .extract
      .map(body => {
        body should matchPattern { case List(MetadataLabel("0", null, _)) => }
        succeed
      })
  }

  "getTransactionMetadataContentJson" should "return sequence of MetadataContentJson" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getTransactionMetadataContentJson("0", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(MetadataContentJson("1c8997f9f0debde5b15fe29f0f18839a64e51c19ccdbe89e2811930d777c9b68", JString("cardano")))
        succeed
      })
  }

  "getTransactionMetadataContentCbor" should "return sequence of MetadataContentJson" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getTransactionMetadataContentCbor("0", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(MetadataContentCbor("1c8997f9f0debde5b15fe29f0f18839a64e51c19ccdbe89e2811930d777c9b68", "\\xa1006763617264616e6f"))
        succeed
      })
  }

  trait TestContext {
    val api: MetadataApi[Future, Any] = new MetadataApiImpl[Future, Any] with TestApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
