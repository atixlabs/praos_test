package io.iohk.praos.domain

import io.iohk.praos.crypto.{Hasher, VerifiableRandomFunction}

/**
  * @note In the new block generation, exist another call to FVRF(EvalProve, ηj || sl || TEST).
  * We understood that the output of that call is stored in the block(Warning: currently we only store the proof) and
  * used to verify later if the stakeholder belongs to the slot leader set for that slot.
  * Q1: Why is not used the output from  (EvalProve, ηj || sl || NONCE) instead? Is not enough with a single call
  * to FVRF in order to verify the slot leader and also generate the partial inputs for the new beacon?
  * Q2: Why this second call doesn’t appear when the Paper describes the protocol in the static case?
  * @note In App.scala is calculated the proof, and then in the BlockFactory is calculated the nonce
  */
case class UnsignedBlock (
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VerifiableRandomFunction#VrfProof,
  nonce       : VerifiableRandomFunction#VerfiableValue)
