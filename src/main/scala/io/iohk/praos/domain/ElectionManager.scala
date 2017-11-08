package io.iohk.praos.domain

import akka.util.ByteString

import scala.math.pow
import io.iohk.praos.crypto.{RandomValue, VerifiableRandomFunction, combineSeeds}

case class ElectionManager(activeSlotCoefficient: Double, vrf: VerifiableRandomFunction) {

  private def probLeader(stakeholderStake: RelativeStake): Double = 1 - pow(1 - activeSlotCoefficient, stakeholderStake)

  private def probLeaderThreshold(randomLength: Int, probLeader: Double) = pow(2, randomLength) * probLeader

  def isStakeHolderLeader(stakeholder: Stakeholder, genesis: Genesis, slotInEpoch: SlotInEpoch):
    Option[(RandomValue, VerifiableRandomFunction#VrfProof)] = {
    // TODO 1: Slot number into a PRNG seed.
    // TODO 2: Check if it is necessary concat another Nonce.
    val randomSeed = combineSeeds(genesis.genesisNonce, ByteString(slotInEpoch.slotNumber))
    val (randomNonce, vrfProof) = vrf.prove(stakeholder.privateKey, randomSeed)

    val probLeader = this.probLeader(RelativeStakeCalculator.calculate(stakeholder.publicKey, genesis.genesisDistribution))
    val probLeaderThreshold = this.probLeaderThreshold(randomNonce.length, probLeader)

    // TODO: FIX ME!
    if (true /*BigInt(1, randomNonce.toArray) < probLeaderThreshold*/) Option((randomNonce, vrfProof))
    else None
  }
}
