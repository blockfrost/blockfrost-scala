package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.NetworkApi.NetworkInfo
import io.blockfrost.sdk.common.SttpSupport
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait NetworkApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getNetworkInformation(implicit formats: Formats, serialization: Serialization): F[ApiResponse[NetworkInfo]]
}

trait NetworkApiImpl[F[_], P] extends NetworkApi[F, P] {
  this: ApiClient[F, P] =>

  override def getNetworkInformation(implicit formats: Formats, serialization: Serialization): F[NetworkApiImpl[F, P]#ApiResponse[NetworkInfo]] =
    get(uri"$host/network")
}

object NetworkApi {
  case class NetworkInfo(supply: Supply, stake: Stake)
  case class Stake(live: String, active: String)
  case class Supply(max: String, total: String, circulating: String)
}
