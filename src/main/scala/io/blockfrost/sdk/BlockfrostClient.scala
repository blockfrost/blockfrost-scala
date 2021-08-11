package io.blockfrost.sdk

import io.blockfrost.sdk.BlockfrostClient.BlockfrostClientConfig
import io.blockfrost.sdk.api.HealthApiImpl
import io.blockfrost.sdk.common.Network
import sttp.client3.SttpBackend

class BlockfrostClient[F[_], P](config: BlockfrostClientConfig[F, P]) extends HealthApiImpl[F, P] with ApiClient[F, P] {
  override implicit val sttpBackend: SttpBackend[F, P] = config.sttpBackend

  override def apiKey: String = config.apiKey

  override def host: String = config.host
}

object BlockfrostClient {
  def apply[F[_], P](sttpBackend: SttpBackend[F, P], network: Network): BlockfrostClient[F, P] = {
    val config = BlockfrostClientConfig(sttpBackend, sys.env.getOrElse("BLOCKFROST_API_KEY", throw new RuntimeException("Api key not found in environment variables")), network.url)
    new BlockfrostClient(config)
  }

  def apply[F[_], P](sttpBackend: SttpBackend[F, P]): BlockfrostClient[F, P] = {
    val apiKey = sys.env.getOrElse("BLOCKFROST_API_KEY", throw new RuntimeException("Api key not found in environment variables"))
    val host = sys.env.getOrElse("BLOCKFROST_HOST", throw new RuntimeException("Api key not found in environment variables"))
    new BlockfrostClient(BlockfrostClientConfig(sttpBackend, apiKey, host))
  }

  case class BlockfrostClientConfig[F[_], P](sttpBackend: SttpBackend[F, P], apiKey: String, host: String)
}
