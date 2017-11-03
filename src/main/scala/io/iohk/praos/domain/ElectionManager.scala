package io.iohk.praos.domain

import scala.math.pow
import io.iohk.praos.crypto.{VerifiableRandomFunction, combineSeeds}
import io.iohk.praos.util.Logger

case class ElectionManager(activeSlotCoefficient: Double, vrf: VerifiableRandomFunction) extends Logger{

  private def probLeader(stakeHolderStake: RelativeStake): Double = 1 - pow(1 - activeSlotCoefficient, stakeHolderStake)

  private def probLeaderThreshold(randomLength: Int, probLeader: Double) = (pow(2, randomLength) * probLeader).toInt

  def isStakeHolderLeader(stakeholder: StakeHolder, genesis: GenesisBlock, slotInEpoch: SlotInEpoch)
    : Option[VerifiableRandomFunction#VrfProof] = {
    // TODO 1: Improve this step with real concatenation and convert slot number into a PRNG seed.
    // TODO 2: Check if it is necessary concat another Nonce.
    val randomSeed = combineSeeds(genesis.genesisNonce, slotInEpoch.slotNumber)
    val (randomValue, proof) = vrf.prove(stakeholder.privateKey, randomSeed)
    val probLeader = this.probLeader(RelativeStakeCalculator.calculate(stakeholder.publicKey, genesis.genesisDistribution))
    val probLeaderThreshold = this.probLeaderThreshold(VerifiableRandomFunction.randomLength, probLeader)
    // log.debug(s"[ElectionManager] - ${randomValue} vs ${probLeaderThreshold}")
    if (randomValue < probLeaderThreshold) Option(proof)
    else None
  }
}
