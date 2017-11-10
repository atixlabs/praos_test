package io.iohk.praos.domain

import io.iohk.praos.crypto._


case class BlockManager(signer: Signer, vrf: VerifiableRandomFunction) {

  def makeBlock(
    slotNumber        : SlotNumber,
    isLeader          : VerifiableRandomFunction#VrfProof,
    transactions      : List[Transaction],
    prevBlockHash     : Option[Hasher#Digest],
    stakeholder       : Stakeholder,
    genesisNonce      : Seed): Block = {

    // TODO: Use slotToSeed when implemented.
    val seed = combineSeeds(combineSeeds(genesisNonce, slotNumber), SeedNonce)

    val blockNonce = vrf.prove(stakeholder.privateKey, seed)
    val unsignedBlock = UnsignedBlock(prevBlockHash, slotNumber, transactions, isLeader, blockNonce)

    signer.signedWith(unsignedBlock, stakeholder.privateKey)
  }

  def verifyBlock(block: Block, genesis: Genesis): Boolean = {
    val (key, unsignedBlock) = signer.stripSignature(block)
    verifyIsLeader(unsignedBlock.slotNumber, genesis.genesisNonce, key, unsignedBlock.proof) &&
      verifyNonce(unsignedBlock.slotNumber, genesis.genesisNonce, key, unsignedBlock.nonce)
  }

  private def verifyIsLeader(
    slotNumber: SlotNumber,
    genesisNonce: Seed,
    key: Key,
    proof: VerifiableRandomFunction#VrfProof): Boolean =
  {
    // TODO: Use slotToSeed when implemented.
    val seed = combineSeeds(combineSeeds(genesisNonce, slotNumber), SeedTest)
    vrf.verify(key, seed, (1 /* true */, proof))
  }

  private def verifyNonce(
    slotNumber: SlotNumber,
    genesisNonce: Seed,
    key: Key,
    nonce: VerifiableRandomFunction#VerfiableValue): Boolean =
  {
    // TODO: Use slotToSeed when implemented.
    val seed = combineSeeds(combineSeeds(genesisNonce, slotNumber), SeedNonce)
    vrf.verify(key, seed, nonce)
  }
}
