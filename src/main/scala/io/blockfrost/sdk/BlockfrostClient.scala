package io.blockfrost.sdk

import io.blockfrost.sdk.ApiClient.ApiKey
import io.blockfrost.sdk.BlockfrostClient.BlockfrostClientConfig
import io.blockfrost.sdk.api.HealthApiImpl
import io.blockfrost.sdk.common.Network
import sttp.client3.SttpBackend

class BlockfrostClient[F[_], P](config: BlockfrostClientConfig[F, P]) extends HealthApiImpl[F, P] with ApiClient[F, P] {
  override implicit val sttpBackend: SttpBackend[F, P] = config.sttpBackend
  override implicit val apiKey: ApiKey = config.apiKey
  override val host: String = config.host
}

object BlockfrostClient {
  def apply[F[_], P](sttpBackend: SttpBackend[F, P], network: Network): BlockfrostClient[F, P] =
    new BlockfrostClient(BlockfrostClientConfig(sttpBackend, getApiKeyFromEnvVariables, network.url))

  def apply[F[_], P](sttpBackend: SttpBackend[F, P]): BlockfrostClient[F, P] =
    new BlockfrostClient(BlockfrostClientConfig(sttpBackend, getApiKeyFromEnvVariables, getHostFromEnvVariables))

  private def getApiKeyFromEnvVariables: String = sys.env.getOrElse("BLOCKFROST_API_KEY", throw new RuntimeException("Api key not found in environment variables"))

  private def getHostFromEnvVariables: String = sys.env.getOrElse("BLOCKFROST_HOST", throw new RuntimeException("Blockfrost host not found in environment variables"))

  case class BlockfrostClientConfig[F[_], P](sttpBackend: SttpBackend[F, P], apiKey: ApiKey, host: String)
}
