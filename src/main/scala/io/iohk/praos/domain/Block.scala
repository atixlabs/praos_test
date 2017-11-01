package io.iohk.praos.domain

import io.iohk.praos.crypto.{Hasher, Seed, Signature, VrfProof}


object Block {

  private case class UnsignedBlock(
    state       : Option[Hasher#Digest],
    slotNumber  : SlotNumber,
    data        : List[Transaction],
    proof       : VrfProof,
    nonce       : (Seed, VrfProof))

  // This function models the makeBlock function in the Praos Formalization.
  def apply(
    slotNumber        : SlotNumber,
    isLeader          : VrfProof,
    transactions      : List[Transaction],
    stakeholderState  : StakeholderState): Block = {



    val signature: Signature =
    Block(stakeholderState.state, slotNumber, transactions, isLeader, signature)
  }
}

case class Block(
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VrfProof,
  nonce       : (Seed, VrfProof),
  signature   : Signature)
