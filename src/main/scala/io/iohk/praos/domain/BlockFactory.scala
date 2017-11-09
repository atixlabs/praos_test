package io.iohk.praos.domain

import akka.util.ByteString
import io.iohk.praos.crypto._

case class BlockFactory(signer: Signer, vrf: VerifiableRandomFunction) {

  def makeBlock(
    slotNumber        : SlotNumber,
    isLeader          : VerifiableRandomFunction#VrfProof,
    transactions      : List[Transaction],
    prevBlockHash     : Option[Hasher#Digest],
    stakeholder       : Stakeholder,
    genesisNonce      : Seed): Block = {

    /**
      * TODO:
      *  1) Check if it is correct to apply a cryptohash like kec256, in order to generate a random seed of length
      *  32 bytes, so VRF current implementation(uses ECDSA 256) can be use.
      *  2) We need to use a slotToSeed random?
      */
    val slotNumberToSeed = ByteString(slotNumber)
    val randomSeed = kec256(combineSeeds(genesisNonce, slotNumberToSeed, SEED_NONCE))

    val blockNonce = vrf.prove(stakeholder.privateKey, randomSeed)
    val unsignedBlock = UnsignedBlock(prevBlockHash, slotNumber, transactions, isLeader, blockNonce)

    signer.signedWith(unsignedBlock, stakeholder.privateKey)
  }
}
