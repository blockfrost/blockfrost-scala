package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.ScriptsApi.{ScriptRedeemer, Script, ScriptHash}
import io.blockfrost.sdk.common.{Config, SortedPageRequest, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait ScriptsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getListOfScripts(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[ScriptHash]]]

  def getSpecificScript(scriptHash: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Script]]

  def getSpecificScriptRedeemers(scriptHash: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[ScriptRedeemer]]]
}

trait ScriptsApiImpl[F[_], P] extends ScriptsApi[F, P] {
  this: ApiClient[F, P] =>

  override def getListOfScripts(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[ScriptHash]]] =
    get(uri"$host/scripts", Some(pageRequest))

  def getSpecificScript(scriptHash: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Script]] =
    get(uri"$host/scripts/$scriptHash")

  override def getSpecificScriptRedeemers(scriptHash: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[ScriptRedeemer]]] =
    get(uri"$host/scripts/$scriptHash/redeemers", Some(pageRequest))
}

object ScriptsApi {
  case class ScriptHash(script_hash: String)
  case class Script(script_hash: String, `type`: String, serialised_size: Option[Double])
  case class ScriptRedeemer(tx_hash: String, tx_index: Int, purpose: String, unit_mem: String, unit_steps: String, fee: String)
}
