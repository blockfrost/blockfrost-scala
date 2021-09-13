package io.blockfrost.sdk.common

import org.json4s.{DefaultFormats, Formats}

object Serialization {
  implicit val formats: Formats = DefaultFormats
}
