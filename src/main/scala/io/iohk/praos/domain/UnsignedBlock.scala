package io.iohk.praos.domain

import io.iohk.praos.crypto.{Hasher, Seed, VrfProof, combineSeeds}


case class UnsignedBlock(
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VrfProof,
  nonce       : (Seed, VrfProof)) {

  def apply(genesisBlock: GenesisBlock): GenesisBlock = {
    val newGenesisDistribution = data.foldLeft(genesisBlock.genesisDistribution)(
      (sd: StakeDistribution, tx: Transaction) => tx.apply(sd)
    )
    val newGenesisNonce = combineSeeds(genesisBlock.genesisNonce, nonce._1)
    GenesisBlock(newGenesisDistribution, newGenesisNonce)
  }
}
