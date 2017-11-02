package io.iohk.praos.domain

import io.iohk.praos.crypto.{Seed, combineSeeds}


/**
  * A genesis block, used at the very start of the blockchain and at the start of each epoch.
  *
  * @param genesisDistribution  The stake distribution to be used.
  * @param genesisNonce  The nonce to be used.
  */
case class GenesisBlock(genesisDistribution: StakeDistribution, genesisNonce: Seed) {
  def applyBlock(block: Block): GenesisBlock = {
    val newGenesisDistribution = block.data.foldLeft(genesisDistribution)(
      (sd: StakeDistribution, tx: Transaction) => applyTransaction(tx, sd)
    )
    val newGenesisNonce = combineSeeds(genesisNonce, block.nonce._1)
    GenesisBlock(newGenesisDistribution, newGenesisNonce)
  }
}
