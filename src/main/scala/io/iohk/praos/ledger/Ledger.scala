package io.iohk.praos.ledger

import io.iohk.praos.crypto.combineSeeds
import io.iohk.praos.domain._

trait Ledger {
  def receiveChain(currentBlockchainState: BlockchainState, newChain: Blockchain): BlockchainState

  def slotEnd(lastSlotState: Genesis, currentBlockChainState: BlockchainState): (Genesis, BlockchainState)

  /**
    * @note It is assumed that blocks (namely, singleton chains) are broadcast instead of chains. This implies that
    *       it cannot be verified that blocks are correctly chained.
    */
  def verifyChain(slotNumber: SlotNumber, newChain: Blockchain, genesisHistory: GenesisHistory): Boolean
}

case class LedgerImpl(consensusResolver: ConsensusResolver, blockManager: BlockManager)
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
        bestBlock => newBestBlockchain.fullBlockchain.filter(_.value.slotNumber > bestBlock.value.slotNumber)
      }
    val newSlotState: Genesis = applyBlockChain(blocksToApply, lastSlotState)
    (newSlotState, newBestBlockchain)
  }

  override def verifyChain(slotNumber: SlotNumber, newChain: Blockchain, genesisHistory: GenesisHistory): Boolean = {
    require(newChain.size == 1, "Only singleton chains are allowed")
    val block = newChain.head

    val genesisAtSlot = genesisHistory.getGenesisAt(slotNumber)

    require(genesisAtSlot.isDefined, s"No genesis associated to $slotNumber in genesis history")
    blockManager.verifyBlock(block, genesisAtSlot.get)
  }

  private def applyBlockChain(newBlockchain: Blockchain, slotState: Genesis): Genesis =
    newBlockchain.foldLeft(slotState)(
      (tempSlotState: Genesis, block: Block) => applyBlock(block, tempSlotState)
    )

  private def applyBlock(block: Block, slotState: Genesis): Genesis = {
    val newStakeDistribution = block.value.data.foldLeft(slotState.genesisDistribution) { (stakeDistribution, tx) =>
      stakeDistribution.applyTransaction(tx)
    }
    val newGenesisNonce = combineSeeds(slotState.genesisNonce, block.value.nonce._1)
    Genesis(newStakeDistribution, newGenesisNonce)
  }
}
