package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.AccountsApi._
import io.blockfrost.sdk.common.{Config, SortedPageRequest, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait AccountsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getSpecificCardanoAddress(stakeAddress: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[AccountAddress]]

  def getAccountRewardHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[RewardHistory]]]

  def getAccountHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[RewardHistory]]]

  def getAccountDelegationHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[DelegationHistory]]]

  def getAccountRegistrationHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[RegistrationHistory]]]

  def getAccountWithdrawalHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[WithdrawalHistory]]]

  def getAccountMirHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[MirHistory]]]

  def getAccountAssociatedAddresses(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Address]]]

  def getAccountAssociatedAssets(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Asset]]]
}

trait AccountsApiImpl[F[_], P] extends AccountsApi[F, P] {
  this: ApiClient[F, P] =>

  override def getSpecificCardanoAddress(stakeAddress: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[AccountAddress]] =
    get(uri"$host/accounts/$stakeAddress")

  override def getAccountRewardHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[RewardHistory]]] =
    get(uri"$host/accounts/$stakeAddress/rewards", Some(pageRequest))

  override def getAccountHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[RewardHistory]]] =
    get(uri"$host/accounts/$stakeAddress/history", Some(pageRequest))

  override def getAccountDelegationHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[DelegationHistory]]] =
    get(uri"$host/accounts/$stakeAddress/delegations", Some(pageRequest))

  override def getAccountRegistrationHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[RegistrationHistory]]] =
    get(uri"$host/accounts/$stakeAddress/registrations", Some(pageRequest))

  override def getAccountWithdrawalHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[WithdrawalHistory]]] =
    get(uri"$host/accounts/$stakeAddress/withdrawals", Some(pageRequest))

  override def getAccountMirHistory(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[MirHistory]]] =
    get(uri"$host/accounts/$stakeAddress/mirs", Some(pageRequest))

  override def getAccountAssociatedAddresses(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Address]]] =
    get(uri"$host/accounts/$stakeAddress/addresses", Some(pageRequest))

  override def getAccountAssociatedAssets(stakeAddress: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Asset]]] =
    get(uri"$host/accounts/$stakeAddress/addresses/assets", Some(pageRequest))
}

object AccountsApi {
  case class AccountAddress(stake_address: String,
                            active: Boolean,
                            active_epoch: Long = 0,
                            controlled_amount: String,
                            rewards_sum: String,
                            withdrawals_sum: String,
                            reserves_sum: String,
                            treasury_sum: String,
                            withdrawable_amount: String,
                            pool_id: String)
  case class RewardHistory(epoch: Int, amount: String, pool_id: String)
  case class DelegationHistory(active_epoch: Int, tx_hash: String, amount: String, pool_id: String)
  case class RegistrationHistory(tx_hash: String, action: String)
  case class WithdrawalHistory(tx_hash: String, amount: String)
  case class MirHistory(tx_hash: String, amount: String)
  case class Address(address: String)
  case class Asset(unit: String, quantity: String)
}
