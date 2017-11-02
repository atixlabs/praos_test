package io.iohk.praos.domain

import io.iohk.praos.crypto._


case class BlockFactory(signer: Signer, vrf: VerifiableRandomFunction) {

  def makeBlock(
    slotNumber        : SlotNumber,
    isLeader          : VerifiableRandomFunction#VrfProof,
    transactions      : List[Transaction],
    stakeholderState  : StakeholderState): Block = {

    val nonce = stakeholderState.genesisCurrent.genesisNonce

    // TODO: Use slotToSeed when implemented.
    val seed = combineSeeds(combineSeeds(nonce, slotNumber), seedNonce)

    val blockNonce = vrf.prove(stakeholderState.privateKey, seed)
    val unsignedBlock = UnsignedBlock(stakeholderState.state, slotNumber, transactions, isLeader, blockNonce)

    signer.signedWith(unsignedBlock, stakeholderState.privateKey)
  }
}
