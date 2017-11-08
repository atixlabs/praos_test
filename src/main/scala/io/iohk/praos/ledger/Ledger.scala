package io.iohk.praos.ledger

import io.iohk.praos.crypto.combineSeeds
import io.iohk.praos.domain._

trait Ledger {
  def receiveChain(currentBlockchainState: BlockchainState, newChain: Blockchain): BlockchainState

  def slotEnd(lastSlotState: Genesis, currentBlockChainState: BlockchainState): (Genesis, BlockchainState)
}

case class LedgerImpl(consensusResolver: ConsensusResolver)
  extends Ledger {

  override def receiveChain(currentBlockchainState: BlockchainState, newChain: Blockchain): BlockchainState =
    BlockchainState.receiveChain(currentBlockchainState, newChain)

  /**
    * TODO: This implementation doesn't consider chain reorgs
    */
  override def slotEnd(lastSlotState: Genesis, currentBlockchainState: BlockchainState): (Genesis, BlockchainState) = {
    val newBestBlockchain: BlockchainState = ConsensusResolver.pickMaxValid(currentBlockchainState)
    val blocksToApply: Blockchain = currentBlockchainState.fullBlockchain.lastOption
      .fold[Blockchain](newBestBlockchain.fullBlockchain) {
        bestBlock => newBestBlockchain.fullBlockchain.filter(_.slotNumber > bestBlock.slotNumber)
      }
    val newSlotState: Genesis = applyBlockChain(blocksToApply, lastSlotState)
    (newSlotState, newBestBlockchain)
  }

  private def applyBlockChain(newBlockchain: Blockchain, slotState: Genesis): Genesis =
    newBlockchain.foldLeft(slotState)(
      (tempSlotState: Genesis, block: Block) => applyBlock(block, tempSlotState)
    )

  private def applyBlock(block: Block, slotState: Genesis): Genesis = {
    val newStakeDistribution = block.data.foldLeft(slotState.genesisDistribution) { (stakeDistribution, tx) =>
      stakeDistribution.applyTransaction(tx)
    }
    val newGenesisNonce = combineSeeds(slotState.genesisNonce, block.nonce)
    Genesis(newStakeDistribution, newGenesisNonce)
  }
}
