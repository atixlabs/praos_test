package io.iohk.praos.domain

import scala.math.{pow, abs}
import io.iohk.praos.crypto.{RandomValue, VerifiableRandomFunction, VrfProof}

case class ElectionManager(activeSlotCoefficient: Double, vrf: VerifiableRandomFunction) {

  def probLeader(stakeHolderStake: RelativeStake): Double = 1 - pow(1 - activeSlotCoefficient, stakeHolderStake)

  def probLeaderThreshold(randomLength: Int, probLeader: Double) = pow(2, randomLength) * probLeader

  def isStakeHolderLeader(stakeholder: StakeHolder, genesis: GenesisBlock, slotInEpoch: SlotInEpoch): Option[VrfProof] = {
    // TODO 1: Improve this step with real concatenation and convert slot number into a PRNG seed.
    // TODO 2: Check if it is necessary concat another Nonce.
    val randomSeed: RandomValue = genesis.genesisNonce + slotInEpoch.slotNumber
    val vrfProof = vrf.prove(stakeholder.privateKey, randomSeed)

    val probLeader = this.probLeader(RelativeStakeCalculator.calculate(stakeholder.publicKey, genesis.genesisDistribution))
    val probLeaderThreshold = this.probLeaderThreshold(VrfProof.randomLength, probLeader)
    println("probLeaderThreshold", probLeaderThreshold, " vs random", abs(vrfProof.random))
    if (abs(vrfProof.random) < probLeaderThreshold) Option(vrfProof)
    else None
  }
}
