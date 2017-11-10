package io.iohk.praos.domain

import io.iohk.praos.beacon.SlotInEpoch

import scala.math.pow
import io.iohk.praos.crypto.{RandomValue, VerifiableRandomFunction, VrfProof, combineSeeds}

case class ElectionManager(activeSlotCoefficient: Double, vrf: VerifiableRandomFunction) {

  private def probLeader(stakeholderStake: RelativeStake): Double = 1 - pow(1 - activeSlotCoefficient, stakeholderStake)

  private def probLeaderThreshold(randomLength: Int, probLeader: Double) = pow(2, randomLength) * probLeader

  def isStakeHolderLeader(stakeholder: Stakeholder, genesis: Genesis, slotInEpoch: SlotInEpoch):
    Option[(RandomValue, VerifiableRandomFunction#VrfProof)] = {
    // TODO 1: Improve this step with real concatenation and convert slot number into a PRNG seed.
    // TODO 2: Check if it is necessary concat another Nonce.
    val randomSeed = combineSeeds(genesis.genesisNonce, slotInEpoch.slotNumber)
    val (nonce, vrfProof) = vrf.prove(stakeholder.privateKey, randomSeed)

    val probLeader = this.probLeader(RelativeStakeCalculator.calculate(stakeholder.publicKey, genesis.genesisDistribution))
    val probLeaderThreshold = this.probLeaderThreshold(VrfProof.randomLength, probLeader)
    if (nonce < probLeaderThreshold) Option((nonce, vrfProof))
    else None
  }
}
