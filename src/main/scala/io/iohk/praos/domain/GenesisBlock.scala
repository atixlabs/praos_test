package io.iohk.praos.domain

import io.iohk.praos.crypto.{Seed, combineSeeds}


/**
  * A genesis block, used at the very start of the blockchain and at the start of each epoch.
  *
  * @param genesisDistribution  The stake distribution to be used.
  * @param genesisNonce  The nonce to be used.
  */
case class GenesisBlock(genesisDistribution: StakeDistribution, genesisNonce: Seed) {

  /**
    * @note The application of a block to a genesis block is *not* a genesis block when the applied block is not the
    *       last block in an epoch, at least at the conceptual level. However, for the sake of simplicity,
    *       GenesisBlocks are also used to store intermediate results when several blocks are applied in sequence.
    */
  def applyBlock(block: Block): GenesisBlock = {
    val newGenesisDistribution = block.data.foldLeft(genesisDistribution)(
      (sd: StakeDistribution, tx: Transaction) => applyTransaction(tx, sd)
    )
    val newGenesisNonce = combineSeeds(genesisNonce, block.nonce._1)
    GenesisBlock(newGenesisDistribution, newGenesisNonce)
  }
}
