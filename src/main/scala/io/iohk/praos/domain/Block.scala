package io.iohk.praos.domain

import io.iohk.praos.crypto.{Hasher, Seed, Signature, VrfProof}


case class Block(
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VrfProof,
  nonce       : (Seed, VrfProof),
  signature   : Signature)
