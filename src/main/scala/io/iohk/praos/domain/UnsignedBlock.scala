package io.iohk.praos.domain

import io.iohk.praos.crypto.{Hasher, Seed, VrfProof}


case class UnsignedBlock(
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VrfProof,
  nonce       : (Seed, VrfProof)) extends Serializable {

  override def toString: String = s"UnsignedBlock($state,$slotNumber,$data,$proof,$nonce)"
}
