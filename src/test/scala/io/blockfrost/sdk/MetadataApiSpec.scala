package io.blockfrost.sdk

import io.blockfrost.sdk.TestContextSupport.RichString
import io.blockfrost.sdk.api.MetadataApi.{MetadataContentCbor, MetadataContentJson, MetadataLabel}
import io.blockfrost.sdk.api.{MetadataApi, MetadataApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.json4s.JsonAST.JValue
import org.json4s.{JObject, JString}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class MetadataApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: MetadataApi[Future, Any]
    val env: String
    val txHash: String
    val jsonMetadata: Option[JValue]
    val cborMetadata: Option[String]
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: MetadataApi[Future, Any] = new MetadataApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
    val txHash: String = "1c8997f9f0debde5b15fe29f0f18839a64e51c19ccdbe89e2811930d777c9b68"
    val jsonMetadata: Option[JValue] = JString("cardano").toSome
    val cborMetadata: Option[String] = "\\xa1006763617264616e6f".some
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: MetadataApi[Future, Any] = new MetadataApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
    val txHash: String = "b92e9b4fa85bc2597f0491700bc2173765918af16deb5683d859941e6d399475"
    val jsonMetadata: Option[JValue] = JObject(List(("La_RepsistancE", JString("Was here")))).toSome
    val cborMetadata: Option[String] = "\\xa100a16e4c615f52657073697374616e6345685761732068657265".some
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getTransactionMetadataLabels [${ctx.env}]" should "return sequence of MetadataLabel" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionMetadataLabels(SortedPageRequest(1))
        .extract
        .map(body => {
          body should matchPattern { case List(MetadataLabel("0", None, _)) => }
          succeed
        })
    }

    s"getTransactionMetadataContentJson [${ctx.env}]" should "return sequence of MetadataContentJson" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionMetadataContentJson("0", SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List(MetadataContentJson(ctx.txHash, ctx.jsonMetadata))
          succeed
        })
    }

    s"getTransactionMetadataContentCbor [${ctx.env}]" should "return sequence of MetadataContentJson" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionMetadataContentCbor("0", SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List(MetadataContentCbor(ctx.txHash, ctx.cborMetadata))
          succeed
        })
    }
  }
}
