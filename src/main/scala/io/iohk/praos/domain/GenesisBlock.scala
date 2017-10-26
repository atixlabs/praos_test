package io.iohk.praos.domain

import io.iohk.praos.crypto.Seed


/**
  * A genesis block, used at the very start of the blockchain and at the start of each epoch.
  * @param genesisDistribution  The stake distribution to be used.
  * @param genesisNonce  The nonce to be used.
  */
case class GenesisBlock(genesisDistribution: StakeDistribution, genesisNonce: Seed)
