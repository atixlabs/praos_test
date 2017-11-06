package io.iohk.praos.domain

import io.iohk.praos.crypto.{Hasher, Seed, VerifiableRandomFunction}


case class UnsignedBlock (
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VerifiableRandomFunction#VrfProof,
  nonce       : (Seed, VerifiableRandomFunction#VrfProof))
