package io.iohk.praos

import io.iohk.praos.crypto.Key
import scala.collection.immutable.Map


package object domain {

  /**
    * An amount of stake held by a stakeholder.
    */
  type Stake = Int

  /**
    * A stake distribution. As noted in Section 2.2.1 of Praos Formalization, stakeholders are identified by their
    * public keys.
    */
  type StakeDistribution = Map[Key, Stake]
  def StakeDistribution(ps: (Key, Stake)*) = Map[Key, Stake](ps: _*)

  def applyTransaction(transaction: Transaction, stakeDistribution: StakeDistribution): StakeDistribution = {
    // TODO: Handle logic errors (i.e. that the transaction makes sense in the initial distribution)
    val resultingSenderStake = stakeDistribution(transaction.senderPublicKey) - transaction.stake
    val resultingRecipientStake = stakeDistribution(transaction.recipientPublicKey) + transaction.stake
    val resultingStakeDistribution = stakeDistribution
      .updated(transaction.senderPublicKey, resultingSenderStake)
      .updated(transaction.recipientPublicKey, resultingRecipientStake)
    resultingStakeDistribution
  }

  /**
    * The fraction of stake held by a stakeholder w.r.t. the total stake held by all stakeholders.
    */
  type RelativeStake = Double

  type SlotNumber = Int

  type Blockchain = List[Block]

  def applyBlockChain(blockchain: Blockchain, genesisBlock: GenesisBlock): GenesisBlock = {
    blockchain.foldLeft(genesisBlock)(
      (tempGenesisBlock: GenesisBlock, block: Block) => tempGenesisBlock.applyBlock(block))
  }
}

