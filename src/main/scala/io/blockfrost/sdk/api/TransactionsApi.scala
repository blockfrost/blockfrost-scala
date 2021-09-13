package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.TransactionsApi._
import io.blockfrost.sdk.common.SttpSupport
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait TransactionsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getSpecificTransaction(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Transaction]]

  def getTransactionUtxos(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[TransactionUtxos]]

  def getTransactionStakeAddressCertificates(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AddressCertificate]]]

  def getTransactionDelegationCertificates(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[DelegationCertificate]]]

  def getTransactionWithdrawal(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[Withdrawal]]]

  def getTransactionMirs(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[TransactionMir]]]

  def getTransactionStakePoolRegistrationAndUpdateCertificates(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[StakePoolCertificate]]]

  def getTransactionStakePoolRetirementCertificates(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[RetirementCertificate]]]

  def getTransactionMetadata(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[TransactionMetadata]]]

  def getTransactionMetadataCbor(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[TransactionMetadataCbor]]]

  def submitTransaction(serializedTransaction: Array[Byte])(implicit formats: Formats, serialization: Serialization): F[ApiResponse[String]]
}

trait TransactionsApiImpl[F[_], P] extends TransactionsApi[F, P] {
  this: ApiClient[F, P] =>

  def getSpecificTransaction(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Transaction]] =
    get(uri"$host/txs/$hash")

  override def getTransactionUtxos(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[TransactionUtxos]] =
    get(uri"$host/txs/$hash/utxos")

  override def getTransactionStakeAddressCertificates(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AddressCertificate]]] =
    get(uri"$host/txs/$hash/stakes")

  override def getTransactionDelegationCertificates(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[DelegationCertificate]]] =
    get(uri"$host/txs/$hash/delegations")

  override def getTransactionWithdrawal(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[Withdrawal]]] =
    get(uri"$host/txs/$hash/withdrawals")

  override def getTransactionMirs(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[TransactionMir]]] =
    get(uri"$host/txs/$hash/mirs")

  override def getTransactionStakePoolRegistrationAndUpdateCertificates(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[StakePoolCertificate]]] =
    get(uri"$host/txs/$hash/pool_updates")

  override def getTransactionStakePoolRetirementCertificates(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[RetirementCertificate]]] =
    get(uri"$host/txs/$hash/pool_retires")

  override def getTransactionMetadata(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[TransactionMetadata]]] =
    get(uri"$host/txs/$hash/metadata")

  override def getTransactionMetadataCbor(hash: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[TransactionMetadataCbor]]] =
    get(uri"$host/txs/$hash/metadata/cbor")

  override def submitTransaction(serializedTransaction: Array[Byte])(implicit formats: Formats, serialization: Serialization): F[ApiResponse[String]] =
    post(uri"$host/tx/submit", serializedTransaction, "application/cbor")
}

object TransactionsApi {
  case class OutputAmount(unit: String, quantity: String)
  case class Transaction(hash: String,
                         block: String,
                         block_height: Int,
                         slot: Int,
                         index: Int,
                         output_amount: Seq[OutputAmount],
                         fees: String,
                         deposit: String,
                         size: Int,
                         invalid_before: String,
                         invalid_hereafter: String,
                         utxo_count: Int,
                         withdrawal_count: Int,
                         mir_cert_count: Int,
                         delegation_count: Int,
                         stake_cert_count: Int,
                         pool_update_count: Int,
                         pool_retire_count: Int,
                         asset_mint_or_burn_count: Int)
  case class Amount(unit: String, quantity: String)
  case class Inputs(address: String, amount: List[Amount], tx_hash: String, output_index: Double)
  case class Outputs(address: String, amount: List[Amount])
  case class TransactionUtxos(hash: String, inputs: List[Inputs], outputs: List[Outputs])
  case class AddressCertificate(cert_index: Double, address: String, registration: Boolean)
  case class DelegationCertificate(cert_index: Double, address: String, pool_id: String, active_epoch: Int)
  case class Withdrawal(address: String, amount: String)
  case class TransactionMir(pot: String,
                            cert_index: Double,
                            address: String,
                            amount: String)
  case class Metadata(url: String,
                      hash: String,
                      ticker: String,
                      name: String,
                      description: String,
                      homepage: String)
  case class Relays(ipv4: String,
                    ipv6: String,
                    dns: String,
                    dns_srv: String,
                    port: Double)
  case class StakePoolCertificate(cert_index: Double,
                                  pool_id: String,
                                  vrf_key: String,
                                  pledge: String,
                                  margin_cost: Double,
                                  fixed_cost: String,
                                  reward_account: String,
                                  owners: List[String],
                                  metadata: Metadata,
                                  relays: List[Relays],
                                  active_epoch: Double)
  case class RetirementCertificate(cert_index: Double, pool_id: String, retiring_epoch: Int)
  case class JsonMetadata(metadata: String, hash: String)
  case class TransactionMetadata(label: String, json_metadata: JsonMetadata)
  case class TransactionMetadataCbor(label: String, cbor_metadata: String)
}