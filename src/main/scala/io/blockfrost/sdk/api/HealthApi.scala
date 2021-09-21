package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.HealthApi.{BackendTime, HealthStatus}
import io.blockfrost.sdk.common.{Config, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait HealthApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getHealthStatus(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[HealthStatus]]

  def getBackendTime(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[BackendTime]]
}

trait HealthApiImpl[F[_], P] extends HealthApi[F, P] {
  this: ApiClient[F, P] =>

  def getHealthStatus(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[HealthStatus]] =
    get(uri"$host/health")

  def getBackendTime(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[BackendTime]] =
    get(uri"$host/health/clock")
}

object HealthApi {
  case class HealthStatus(is_healthy: Boolean)
  case class BackendTime(server_time: Long)
}
