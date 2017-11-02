package io.iohk.praos.domain

import akka.util.ByteString
import io.iohk.praos.crypto._


object BlockFactory {

  def makeBlock(
    slotNumber        : SlotNumber,
    isLeader          : VrfProof,
    transactions      : List[Transaction],
    stakeholderState  : StakeholderState): Block = {


    // TODO: Fix VrfProof issue first.
    val nonce = (123, VrfProof(123, ByteString("abc"))) // vrf(...)
    val unsignedBlock = UnsignedBlock(stakeholderState.state, slotNumber, transactions, isLeader, nonce)

    // FIXME: SignerImpl is hard-coded for simplicity.
    SignerImpl.signedWith(unsignedBlock, stakeholderState.privateKey)
  }
}
