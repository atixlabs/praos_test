package io.iohk.praos

import akka.util.ByteString
import io.iohk.praos.crypto.{Hasher, PredefinedHasher, Signature, Signed}


package object domain {

  /**
    * The fraction of stake held by a stakeholder w.r.t. the total stake held by all stakeholders.
    */
  type RelativeStake = Double

  type SlotNumber = Int

  type Block = Signed[UnsignedBlock]
  def Block(unsignedBlock: UnsignedBlock, signature: Signature) = Signed[UnsignedBlock](unsignedBlock, signature)
  def blockHash(block: Block): Hasher#Digest = PredefinedHasher("MD5").hash(ByteString(block.value.slotNumber))

  /**
    * Is important note that the Haskell specification variate from the standard definition of Blockchain.
    * Here its defined as a list of blocks, and not referred to the "unique" blockchain inself.
    *
    * @note For each blockchain, the order is assume from oldest to latest.
    */
  type Blockchain = Seq[Block]

}

