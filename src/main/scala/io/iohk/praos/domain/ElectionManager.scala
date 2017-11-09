package io.iohk.praos.domain

import akka.util.ByteString

import scala.math.pow
import io.iohk.praos.crypto.{RandomValue, VerifiableRandomFunction, combineSeeds, kec256, SEED_TEST}

case class ElectionManager(activeSlotCoefficient: Double, vrf: VerifiableRandomFunction) {

  def isStakeHolderLeader(stakeholder: Stakeholder, genesis: Genesis, slotInEpoch: SlotInEpoch):
    Option[(RandomValue, VerifiableRandomFunction#VrfProof)] = {

    val slotNumberToSeed = ByteString(slotInEpoch.slotNumber)
    /**
      * TODO:
      *  1) Check if it is correct to apply a cryptohash like kec256, in order to generate a random seed of length
      *  32 bytes, so VRF current implementation(uses ECDSA 256) can be use.
      *  2) Check if it is necessary concat with SEED_TEST.
      */
    val randomSeed = kec256(combineSeeds(genesis.genesisNonce, slotNumberToSeed, SEED_TEST))

    val (randomNonce, vrfProof) = vrf.prove(stakeholder.privateKey, randomSeed)

    val probLeader = this.probLeader(RelativeStakeCalculator.calculate(stakeholder.publicKey, genesis.genesisDistribution))
    val probLeaderThreshold = this.probLeaderThreshold(randomNonce.length * BYTE_LENGTH, probLeader)

    val randomNonceAsPositiveBigInt = BigInt(1, randomNonce.toArray)
    if (randomNonceAsPositiveBigInt < probLeaderThreshold) Option((randomNonce, vrfProof))
    else None
  }

  /**
    * @return The probability of a given stakeholder to be choose as leader
    */
  private def probLeader(stakeholderStake: RelativeStake): Double = 1 - pow(1 - activeSlotCoefficient, stakeholderStake)
  /**
    * @param randomLength length in bits
    * @return the representation of the probability in the dimension of the random nonce vector
    */
  private def probLeaderThreshold(randomLength: Int, probLeader: Double): BigInt =
    BigDecimal(pow(2, randomLength) * probLeader).toBigInt()

  private val BYTE_LENGTH = 8
}
