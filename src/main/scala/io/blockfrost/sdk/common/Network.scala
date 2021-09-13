package io.blockfrost.sdk.common

trait Network {
  def url: String
}

case object Mainnet extends Network {
  def url: String = "https://cardano-mainnet.blockfrost.io/api/v0"
}

case object Testnet extends Network {
  def url: String = "https://cardano-testnet.blockfrost.io/api/v0"
}

case object IPFS extends Network {
  def url: String = "https://ipfs.blockfrost.io/api/v0"
}
