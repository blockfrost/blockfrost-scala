[![Scala CI with SBT](https://github.com/blockfrost/blockfrost-scala/actions/workflows/actions.yml/badge.svg?branch=master)](https://github.com/blockfrost/blockfrost-scala/actions/workflows/actions.yml)

# blockfrost-scala

<br/>

<p align="center">A Scala SDK for <a href="https://blockfrost.io">Blockfrost.io</a> API.</p>
<p align="center">Compatible with Blockfrost.io OpenAPI version 0.1.25</p>
<p align="center">
  <a href="#getting-started">Getting started</a> •
  <a href="#installation">Installation</a> •
  <a href="#usage">Usage</a>
</p>
<br>

## Getting started

To use this SDK, you first need to log in to [blockfrost.io](https://blockfrost.io), create your project and retrieve the API token.

<img src="https://i.imgur.com/smY12ro.png">

## Installation

To use blockfrost-scala as a dependency in your scala project you should clone this repository to your dev environment, build artifcat and publish it to your local maven repository:

```
$> git clone https://github.com/blockfrost/blockfrost-scala.git
$> cd blockfrost scala
$> sbt publishLocal
```

## Usage

Add blockfrost-scala dependency to your build.sbt

```
libraryDependencies += "io.blockfrost" %% "blockfrost-scala" % "0.1.0"
```

This library require any sttp backend application in your classpath. Full list of supported backends: [Supported backends](https://sttp.softwaremill.com/en/latest/backends/summary.html).

This library has built-in support for Future sttp backend to transform response type from `Future[Response[Either[ResponseException[String, Exception], R]]]` to `Future[R]` and fetch all pages from APIs with pagination (see `FuturePaginationSupport` and `FutureResponseConverter` classes). Anyway, this library is designed to use it with any other functional effect wrapper like [ZIO](https://github.com/zio/zio), [Monix](https://monix.io/), [Scalaz](https://github.com/scalaz/scalaz), [cats-effect](https://github.com/typelevel/cats-effect), [fs2](https://github.com/typelevel/fs2) etc.

For this example we will use [async-http-client](https://sttp.softwaremill.com/en/latest/backends/future.html#using-async-http-client) based on Future:

```
libraryDependencies += "com.softwaremill.sttp.client3" %% "async-http-client-backend" % $version,
```

**Note:** Replace '$version' with the correct version number

#### Following services are available

- **Cardano**
  - AccountsApi
  - AddressesApi
  - AssetsApi
  - BlockApi
  - EpochsApi
  - HealthApi
  - LedgerApi
  - MetadataApi
  - MetricsApi
  - NetworkApi
  - PoolsApi
  - TransactionsApi
- **IPFS**
  - IpfsApi
- **Nut.link**
  - NutlinkApi

#### Example application with blockfrost-sdk and Future sttp backend
  
```
import io.blockfrost.sdk.BlockfrostClient
import io.blockfrost.sdk.api.{AssetsApi, BlockApi, IpfsApi}
import io.blockfrost.sdk.common._
import io.blockfrost.sdk.effect.FuturePaginationSupport
import io.blockfrost.sdk.effect.FutureResponseConverter.FutureResponseOps
import org.json4s.jackson.Serialization
import org.json4s.{DefaultFormats, Formats}
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends App {

  implicit val formats: Formats = DefaultFormats
  implicit val serialization: Serialization.type = Serialization
  // SDK configuration for http client, concurrent requests and rate limiter
  implicit val sdkConfig: Config = Config(threadCount = 5, rateLimitPauseMillis = 500, readTimeoutMillis = 5000)
  // Explicitly pass you API key to the BlockfrostClient or set it to BLOCKFROST_API_KEY env variable
  val apiKey = "your-api-key"
  val sttpBackend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

  // Instantiate Mainnet BlockfrostClient
  val cardanoClient = BlockfrostClient[Future, Any](sttpBackend, Mainnet, apiKey)

  // Get latest Cardano block
  val latestBlockF: Future[BlockApi.BlockContent] = cardanoClient.getLatestBlock.extract

  // Get paginated assets: 10 elements from second page, sorted ascending
  val assetsPageF: Future[Seq[AssetsApi.AssetShort]] = cardanoClient.getAssets(SortedPageRequest.apply(count = 10, page = 2, order = Asc)).extract

  // Concurrently get all assets from all pages using 5 threads (configured in sdkConfig variable)
  val allAssetsF: Future[Seq[AssetsApi.AssetShort]] = FuturePaginationSupport.getAllPages((pr: PageRequest) => cardanoClient.getAssets(pr.asInstanceOf[SortedPageRequest]).extract, Some(Asc))

  // Instantiate IPFS BlockfrostClient
  val ipfsClient = BlockfrostClient[Future, Any](sttpBackend, IPFS, apiKey)

  // Create IPFS object and pin it
  val file = new File("<path to file>")
  val pinnedIpfsObjectF: Future[IpfsApi.PinnedObject] = ipfsClient.addObject(file).extract.flatMap { ipfsObject =>
    ipfsClient.pinObject(ipfsObject.ipfs_hash).extract
  }
}
```
