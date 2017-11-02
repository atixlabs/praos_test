package io.iohk.praos.domain

import akka.util.ByteString
import io.iohk.praos.crypto._


case class Block(
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VrfProof,
  nonce       : (Seed, VrfProof),
  signature   : Signature) {

  // TODO: Implement it
  def blockHash: ByteString = ByteString(slotNumber)
}
