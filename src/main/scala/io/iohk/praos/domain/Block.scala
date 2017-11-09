package io.iohk.praos.domain

import akka.util.ByteString
import io.iohk.praos.crypto.{Hasher, PredefinedHasher, Signature, Signed}


object Block {
  def apply(unsignedBlock: UnsignedBlock, signature: Signature) = Signed[UnsignedBlock](unsignedBlock, signature)
  def hashValue(block: Block): Hasher#Digest = PredefinedHasher("MD5").hash(ByteString(block.value.slotNumber))
}
