package io.iohk.praos.domain

import io.iohk.praos.crypto._


case class BlockFactory(vrf: VerifiableRandomFunction) {

  def makeBlock(
    slotNumber        : SlotNumber,
    isLeader          : VerifiableRandomFunction#VrfProof,
    transactions      : List[Transaction],
    prevBlockHash     : Option[Hasher#Digest],
    stakeholder       : StakeHolder,
    genesisNonce      : Seed): Block = {

    // TODO: Use slotToSeed when implemented.
    val seed = combineSeeds(combineSeeds(genesisNonce, slotNumber), SeedNonce)

    val blockNonce = vrf.prove(stakeholder.privateKey, seed)
    val unsignedBlock = Block(prevBlockHash, slotNumber, transactions, isLeader, blockNonce, null)

    BlockSigner.signedWith(unsignedBlock, stakeholder.privateKey)
  }
}
