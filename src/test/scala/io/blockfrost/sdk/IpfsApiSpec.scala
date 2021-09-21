package io.blockfrost.sdk

import io.blockfrost.sdk.api.IpfsApi.{IpfsObject, PinnedObject}
import io.blockfrost.sdk.api.{IpfsApi, IpfsApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.effect.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.File
import scala.concurrent.Future

class IpfsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "addObject" should "return IpfsObject" in genericTestContext[TestContext] { ctx =>
    val path = getClass.getResource("/ipfs_test.file").getPath
    val file = new File(path)
    ctx.api
      .addObject(file)
      .extract
      .flatMap(body => {
        body should matchPattern { case IpfsObject("ipfs_test.file", _, _) => }
        ctx.api.removePinnedObject(body.ipfs_hash).extract
      }).map(_ => succeed)
  }

  "getObject" should "return IPFS object as a byte array" in genericTestContext[TestContext] { ctx =>
    val path = getClass.getResource("/ipfs_test.file").getPath
    val file = new File(path)
    for {
      ipfsObject <- ctx.api.addObject(file).extract
      ipfsFileData <- ctx.api.getObject(ipfsObject.ipfs_hash)
      _ <- ctx.api.removePinnedObject(ipfsObject.ipfs_hash).extract
    } yield {
      ipfsFileData.body.right.get.nonEmpty shouldBe true
      succeed
    }
  }

  "pinObject" should "return PinnedObject" in genericTestContext[TestContext] { ctx =>
    val path = getClass.getResource("/ipfs_test.file").getPath
    val file = new File(path)
    for {
      ipfsObject <- ctx.api.addObject(file).extract
      pinnedObject <- ctx.api.pinObject(ipfsObject.ipfs_hash).extract
      _ <- ctx.api.removePinnedObject(ipfsObject.ipfs_hash).extract
    } yield {
      pinnedObject should matchPattern { case PinnedObject(ipfsObject.ipfs_hash, _, _, _, _) => }
    }
  }

  "listPinnedObjects" should "return sequence of PinnedObject" in genericTestContext[TestContext] { ctx =>
    val path = getClass.getResource("/ipfs_test.file").getPath
    val file = new File(path)
    for {
      ipfsObject <- ctx.api.addObject(file).extract
      _ <- ctx.api.pinObject(ipfsObject.ipfs_hash).extract
      result <- ctx.api.listPinnedObjects(SortedPageRequest(1)).extract
      _ <- ctx.api.removePinnedObject(ipfsObject.ipfs_hash).extract
    } yield {
      result.nonEmpty shouldBe true
    }
  }

  "getPinnedObject" should "return PinnedObject" in genericTestContext[TestContext] { ctx =>
    val path = getClass.getResource("/ipfs_test.file").getPath
    val file = new File(path)
    for {
      ipfsObject <- ctx.api.addObject(file).extract
      _ <- ctx.api.pinObject(ipfsObject.ipfs_hash).extract
      result <- ctx.api.getPinnedObject(ipfsObject.ipfs_hash).extract
      _ <- ctx.api.removePinnedObject(ipfsObject.ipfs_hash).extract
    } yield {
      result should matchPattern { case PinnedObject(ipfsObject.ipfs_hash, _, _, _, _) => }
    }
  }

  "removePinnedObject" should "return PinnedObject" in genericTestContext[TestContext] { ctx =>
    val path = getClass.getResource("/ipfs_test.file").getPath
    val file = new File(path)
    for {
      ipfsObject <- ctx.api.addObject(file).extract
      _ <- ctx.api.pinObject(ipfsObject.ipfs_hash).extract
      result <- ctx.api.removePinnedObject(ipfsObject.ipfs_hash).extract
    } yield {
      result should matchPattern { case PinnedObject(ipfsObject.ipfs_hash, _, _, _, _) => }
    }
  }

  trait TestContext {
    val api: IpfsApi[Future, Any] = new IpfsApiImpl[Future, Any] with TestIpfsApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
