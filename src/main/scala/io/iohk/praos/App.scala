package io.iohk.praos

import scala.util.Random
import io.iohk.praos.domain._

object App {

  def main(args: Array[String]): Unit = {
    // Setup an Stake Distribution
    val stakeholder1 = crypto.generateKeyPair(Random.nextInt())
    val stakeholder2 = crypto.generateKeyPair(Random.nextInt())
    val stakeholder3 = crypto.generateKeyPair(Random.nextInt())
    val stakeHolders = List(stakeholder1, stakeholder2, stakeholder3)
    val stakeDistribution = StakeDistribution(stakeholder1._1 -> 5, stakeholder2._1 -> 6, stakeholder3._1 -> 4)
    // Setup Environment
    val env: Environment = Environment(
      stakeDistribution,
      epochLength = 10,
      slotDurationInMilliseconds = 3000,
      timeProvider = new TimeProvider(initialTime = 0)
    )
    val slotInEpochCalculator = new SlotInEpochCalculator(
      epochLength = env.epochLength,
      slotDurationInMilliseconds = env.slotDurationInMilliseconds
    )
    var genesis = None
    // Run Protocol
    /* while (true) {
      val slotInEpoch: SlotInEpoch = slotInEpochCalculator.calculate(env.timeProvider)
      if (slotInEpoch.firstInEpoch) {
       genesis = VirtualGenesis.generate(slotInEpoch, env, ..)
      }
      stakeHolders.foreach(stakeholder => {
        val newBlock: Option[Block] = BlockGenerator.generateBlock(slotInEpoch, stakeholder, genesis, ..)
        // TODO step1: If there is a new block added to the Blockchain
        // TODO step2: Emit the new block to the channel
      })
      // In parallel
      // TODO: Receive chains (new blocks) and verify them, uptating the current state.

      env.timeProvider.advance(env.slotDurationInMilliseconds)
    } */
  }
}