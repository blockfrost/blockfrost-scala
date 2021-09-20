package io.blockfrost.sdk.common

case class Config(threadCount: Int = 10, rateLimitPauseMillis: Int = 100, readTimeoutMillis: Int = 2000)
