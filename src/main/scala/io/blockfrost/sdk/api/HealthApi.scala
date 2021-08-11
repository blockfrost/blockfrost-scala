package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.HealthApi.{BackendTime, HealthStatus}
import io.blockfrost.sdk.common.SttpSupport
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait HealthApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getHealthStatus(implicit formats: Formats, serialization: Serialization): F[ApiResponse[HealthStatus]]

  def getBackendTime(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BackendTime]]
}

trait HealthApiImpl[F[_], P] extends HealthApi[F, P] {
  this: ApiClient[F, P] =>

  def getHealthStatus(implicit formats: Formats, serialization: Serialization): F[ApiResponse[HealthStatus]] =
    get[F, P, HealthStatus](uri"$host/health")

  def getBackendTime(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BackendTime]] =
    get[F, P, BackendTime](uri"$host/health/clock")
}

object HealthApi {
  case class HealthStatus(is_healthy: Boolean)
  case class BackendTime(server_time: Long)
}
