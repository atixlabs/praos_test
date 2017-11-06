package io.iohk.praos.domain

import akka.util.ByteString
import io.iohk.praos.crypto._


case class Block(
  state : Option[Hasher#Digest],
  slotNumber : SlotNumber,
  data : List[Transaction],
  proof : VerifiableRandomFunction#VrfProof,
  nonce : (Seed, VerifiableRandomFunction#VrfProof),
  signature: Signature) {

  lazy val hashValue: Hasher#Digest = PredefinedHasher("MD5").hash(ByteString(slotNumber))
}
