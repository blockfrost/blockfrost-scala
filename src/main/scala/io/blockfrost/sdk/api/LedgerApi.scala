package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.LedgerApi.Genesis
import io.blockfrost.sdk.common.SttpSupport
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait LedgerApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getBlockchainGenesis(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Genesis]]
}

trait LedgerApiImpl[F[_], P] extends LedgerApi[F, P] {
  this: ApiClient[F, P] =>

  override def getBlockchainGenesis(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Genesis]] =
    get(uri"$host/genesis")
}

object LedgerApi {
  case class Genesis(active_slots_coefficient: Double,
                     update_quorum: Int,
                     max_lovelace_supply: String,
                     network_magic: Int,
                     epoch_length: Int,
                     system_start: Int,
                     slots_per_kes_period: Int,
                     slot_length: Int,
                     max_kes_evolutions: Int,
                     security_param: Int)
}
