package io.iohk.praos

import akka.util.ByteString
import io.iohk.praos.crypto._

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
    if (transaction.senderPublicKey == transaction.recipientPublicKey) { // "self" transaction
      stakeDistribution
    } else {
      // TODO: Handle logic errors (i.e. that the transaction makes sense in the initial distribution)
      val resultingSenderStake = stakeDistribution(transaction.senderPublicKey) - transaction.stake
      val resultingRecipientStake = stakeDistribution(transaction.recipientPublicKey) + transaction.stake
      stakeDistribution + (transaction.senderPublicKey -> resultingSenderStake) +
        (transaction.recipientPublicKey -> resultingRecipientStake)
    }
  }

  /**
    * The fraction of stake held by a stakeholder w.r.t. the total stake held by all stakeholders.
    */
  type RelativeStake = Double

  type SlotNumber = Int

  type Block = Signed[UnsignedBlock]
  def Block(unsignedBlock: UnsignedBlock, signature: Signature) = Signed[UnsignedBlock](unsignedBlock, signature)
  def blockHash(block: Block): Hasher#Digest = PredefinedHasher("MD5").hash(ByteString(block.value.slotNumber))

  /**
    *  Is important denote that the Haskell specification variate from the standard definition of Blockchain.
    *  Here its defined as a list of blocks, and not referred to the "unique" blockchain inself.
    *  @note For each blockchain, the order is assume from oldest to latest.
    */
  type Blockchain = List[Block]
  def Blockchain(bs: Block*) = List[Block](bs: _*)

  def applyBlockChain(blockchain: Blockchain, genesisBlock: GenesisBlock): GenesisBlock = {
    blockchain.foldLeft(genesisBlock)(
      (tempGenesisBlock: GenesisBlock, block: Block) => tempGenesisBlock.applyBlock(block))
  }
}

