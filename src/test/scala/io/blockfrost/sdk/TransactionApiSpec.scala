package io.blockfrost.sdk

import com.avsystem.commons.serialization.cbor.CborOutput
import io.blockfrost.sdk.api.TransactionsApi.{Transaction, TransactionUtxos}
import io.blockfrost.sdk.api.{TransactionsApi, TransactionsApiImpl}
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class TransactionApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: TransactionsApi[Future, Any]
    val env: String
    val transactionHash: String
    val signedTxn: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: TransactionsApi[Future, Any] = new TransactionsApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
    val transactionHash = "e067ca567df4920f4ac3babc4d805d2afe860e21aa7f6f78dbe8538caf9d8262"
    val signedTxn = "invalid"
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: TransactionsApi[Future, Any] = new TransactionsApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
    val transactionHash = "9d2495ca751971571115512c3054d8b5a2f8be7b03923f1f9f416a543e0ec4b7"
    val signedTxn = "invalid"
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getSpecificTransaction [${ctx.env}]" should "return Transaction" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificTransaction(ctx.transactionHash)
        .extract
        .map(body => {
          body should matchPattern { case Transaction(ctx.transactionHash, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => }
          succeed
        })
    }

    s"getTransactionUtxos [${ctx.env}]" should "return TransactionUtxos" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionUtxos(ctx.transactionHash)
        .extract
        .map(body => {
          body should matchPattern { case TransactionUtxos(ctx.transactionHash, _, _) => }
          succeed
        })
    }

    s"getTransactionStakeAddressCertificates [${ctx.env}]" should "return seq of AddressCertificate" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionStakeAddressCertificates(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"getTransactionDelegationCertificates [${ctx.env}]" should "return seq of DelegationCertificate" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionDelegationCertificates(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"getTransactionWithdrawal [${ctx.env}]" should "return seq of Withdrawal" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionWithdrawal(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"getTransactionMirs [${ctx.env}]" should "return seq of TransactionMir" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionMirs(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"getTransactionStakePoolRegistrationAndUpdateCertificates [${ctx.env}]" should "return seq of StakePoolCertificate" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionStakePoolRegistrationAndUpdateCertificates(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"getTransactionStakePoolRetirementCertificates [${ctx.env}]" should "return seq of RetirementCertificate" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionStakePoolRetirementCertificates(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"getTransactionMetadata [${ctx.env}]" should "return seq of TransactionMetadata" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionMetadata(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"getTransactionMetadataCbor [${ctx.env}]" should "return seq of TransactionMetadataCbor" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionMetadataCbor(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"getTransactionRedeemers [${ctx.env}]" should "return seq of TransactionRedeemer" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getTransactionRedeemers(ctx.transactionHash)
        .extract
        .map(body => {
          body shouldBe Seq()
          succeed
        })
    }

    s"submitTransaction [${ctx.env}]" should "throw error on invalid request payload" in genericTestContext[TestContext] { ctx =>
      val requestBody: Array[Byte] = CborOutput.write(ctx.signedTxn)
      ctx.api
        .submitTransaction(requestBody)
        .extract
        .map(_ => {
          fail()
        }).recover({
        case x if x.getMessage.contains("transaction read error RawCborDecodeError") => succeed
        case _ => fail()
      })
    }
  }
}
