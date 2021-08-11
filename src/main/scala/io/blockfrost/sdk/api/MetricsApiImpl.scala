package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.MetricsApi.{EndpointMetric, Metric}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait MetricsApiImpl[F[_], P] extends MetricsApi[F, P] {
  this: ApiClient[F, P] =>

  def getUsageMetrics(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[Metric]]] =
    get(uri"$host/metrics", apiKey)

  def getEndpointUsageMetrics(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[EndpointMetric]]] =
    get(uri"$host/metrics/endpoints", apiKey)
}
