package io.iohk.praos.domain

import akka.util.ByteString
import io.iohk.praos.crypto._


case class Block(
  state       : Option[ByteString],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VerifiableRandomFunction#VrfProof,
  nonce       : Seed,
  signature   : Signature) {

  // TODO: Implement it
  def blockHash: ByteString = ByteString(slotNumber)
}
