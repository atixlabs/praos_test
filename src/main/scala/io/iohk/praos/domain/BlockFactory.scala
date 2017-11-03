package io.iohk.praos.domain

import io.iohk.praos.crypto._


case class BlockFactory(signer: Signer, vrf: VerifiableRandomFunction) {

  def makeBlock(
    slotNumber        : SlotNumber,
    isLeader          : VerifiableRandomFunction#VrfProof,
    transactions      : List[Transaction],
    prevBlockHash     : Option[Hasher#Digest],
    stakeholder       : StakeHolder,
    genesisNonce      : Seed): Block = {

    // TODO: Use slotToSeed when implemented.
    val seed = combineSeeds(combineSeeds(genesisNonce, slotNumber), seedNonce)

    val blockNonce = vrf.prove(stakeholder.privateKey, seed)
    val unsignedBlock = UnsignedBlock(prevBlockHash, slotNumber, transactions, isLeader, blockNonce)

    signer.signedWith(unsignedBlock, stakeholder.privateKey)
  }
}
