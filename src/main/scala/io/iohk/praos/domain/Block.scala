package io.iohk.praos.domain

import io.iohk.praos.crypto.{Hasher, Seed, Signature, VrfProof, combineSeeds}


case class Block(
  state       : Option[Hasher#Digest],
  slotNumber  : SlotNumber,
  data        : List[Transaction],
  proof       : VrfProof,
  nonce       : (Seed, VrfProof),
  signature   : Signature) {

  def apply(genesisBlock: GenesisBlock): GenesisBlock = {
    val newGenesisDistribution = data.foldLeft(genesisBlock.genesisDistribution)(
      (sd: StakeDistribution, tx: Transaction) => tx.apply(sd)
    )
    val newGenesisNonce = combineSeeds(genesisBlock.genesisNonce, nonce._1)
    GenesisBlock(newGenesisDistribution, newGenesisNonce)
  }
}
