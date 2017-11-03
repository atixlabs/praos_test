package io.iohk.praos.domain

import io.iohk.praos.crypto.combineSeeds

trait Ledger {
  def receiveChain(newChain: Blockchain): Ledger

  def slotEnd: Ledger
}


case class LedgerImpl(
  bestBlock: GenesisBlock,
  blockchainState: BlockchainState,
  consensusResolver: ConsensusResolver)
  extends Ledger {

  override def receiveChain(newChain: Blockchain): Ledger =
    copy(blockchainState = BlockchainState.receiveChain(blockchainState, newChain))

  override def slotEnd: Ledger = {
    // This code doesn't consider chain reorgs
    val newBestBlockchain: BlockchainState = ConsensusResolver.pickMaxValid(blockchainState)
    val blocksToApply: Blockchain = blockchainState.fullBlockchain.lastOption
      .fold[Blockchain](newBestBlockchain.fullBlockchain) {
        bestBlock => newBestBlockchain.fullBlockchain.filter(_.slotNumber > bestBlock.slotNumber)
      }
    val newBestBlock: GenesisBlock = applyBlockChain(blocksToApply, bestBlock)
    copy (
      bestBlock = newBestBlock,
      blockchainState = newBestBlockchain
    )
  }

  private def applyBlockChain(newBlockchain: Blockchain, genesisBlock: GenesisBlock): GenesisBlock =
    newBlockchain.foldLeft(genesisBlock)(
      (tempGenesisBlock: GenesisBlock, block: Block) => applyBlock(tempGenesisBlock, block)
    )


  /**
    * @note The application of a block to a genesis block is *not* a genesis block when the applied block is not the
    *       last block in an epoch, at least at the conceptual level. However, for the sake of simplicity,
    *       GenesisBlocks are also used to store intermediate results when several blocks are applied in sequence.
    */
  private def applyBlock(genesisBlock: GenesisBlock, block: Block): GenesisBlock = {
    import genesisBlock._
    val newGenesisDistribution = block.data.foldLeft(genesisDistribution) { (stakeDistribution, tx) =>
      stakeDistribution.applyTransaction(tx)
    }
    val newGenesisNonce = combineSeeds(genesisNonce, block.nonce._1)
    GenesisBlock(newGenesisDistribution, newGenesisNonce)
  }
}
