package io.iohk.praos.domain

import io.iohk.praos.crypto._


case class Block(
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VrfProof,
  nonce       : (Seed, VrfProof),
  signature   : Signature) {

  // TODO: Implement it
  def blockHash = akka.util.ByteString("abc")
}
