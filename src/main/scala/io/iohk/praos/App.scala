package io.iohk.praos

import io.iohk.praos.domain._
import io.iohk.praos.crypto.generateNewRandomValue

object App {

  def main(args: Array[String]): Unit = {
    // Setup an Stake Distribution
    val (publicKey1, privateKey1) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder1 = StakeHolder(privateKey1, publicKey1)
    val (publicKey2, privateKey2) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder2 = StakeHolder(privateKey2, publicKey2)
    val (publicKey3, privateKey3) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder3 = StakeHolder(privateKey3, publicKey3)
    val stakeHolders = List(stakeholder1, stakeholder2, stakeholder3)
    val stakeDistribution = StakeDistribution(stakeholder1.publicKey -> 5, stakeholder2.publicKey -> 6, stakeholder3.publicKey -> 4)
    // Setup Environment
    val env: Environment = Environment(
      stakeDistribution,
      epochLength = 10,
      slotDurationInMilliseconds = 3000,
      timeProvider = new TimeProvider(initialTime = 0),
      activeSlotCoefficient = 0.70
    )
    val slotInEpochCalculator = SlotInEpochCalculator(
      epochLength = env.epochLength,
      slotDurationInMilliseconds = env.slotDurationInMilliseconds
    )
    var genesis = None
    // Run Protocol
    /* while (true) {
      val slotInEpoch: SlotInEpoch = slotInEpochCalculator.calculate(env.timeProvider)
      if (slotInEpoch.firstInEpoch) {
       genesis = VirtualGenesis.generate(slotInEpoch, blockchain)
      }
      stakeHolders.foreach(stakeholder => {
        val isLeaderProof: Option[VrfProof]) =
          ElectionManager(env.activeSlotCoefficient, VerifiableRandomFunctionStubImpl).isStakeHolderLeader(stakeholder, genesis, slotInEpoch)
        if (isLeaderProof.isDefined) {
          val newBlock: Block = BlockGenerator.generateBlock(stakeholder, isLeaderProof, slotInEpoch, genesis, blockchain)
          // TODO step1: If there is a new block added to the Blockchain
          // TODO step2: Emit the new block to the channel
        }
      })
      // In parallel
      // TODO: Receive chains (new blocks) and verify them, uptating the current state.

      env.timeProvider.advance(env.slotDurationInMilliseconds)
    } */
  }
}