package io.iohk.praos

import akka.util.ByteString
import io.iohk.praos.domain._
import io.iohk.praos.crypto.{VerifiableRandomFunctionStubImpl, VrfProof, generateNewRandomValue}

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
      k = 2,
      slotDurationInMilliseconds = 3000,
      timeProvider = new TimeProvider(initialTime = 0),
      activeSlotCoefficient = 0.70,
      initialNonce = generateNewRandomValue()
    )
    val slotInEpochCalculator = SlotInEpochCalculator(
      epochLength = env.epochLength,
      slotDurationInMilliseconds = env.slotDurationInMilliseconds
    )
    val virtualGenesis = VirtualGenesis(env.k)

    var blockchainState = BlockchainState(
      fullBlockchain = List.empty[Block],
      receivedChains = List.empty[Blockchain]
    )

    // Init Protocol
    var genesis = GenesisBlock(env.initialStakeDistribution, env.initialNonce)
    var slotInEpoch: SlotInEpoch = slotInEpochCalculator.calculate(env.timeProvider)
    // Run Protocol only one epoch
    while (slotInEpoch.slotNumber < env.epochLength) {
      stakeHolders.foreach(stakeholder => {
        val isLeaderProof: Option[VrfProof] =
          ElectionManager(env.activeSlotCoefficient, VerifiableRandomFunctionStubImpl)
            .isStakeHolderLeader(stakeholder, genesis, slotInEpoch)
        if (isLeaderProof.isDefined) {
          val newBlock = null
          val newChain: Blockchain = List(newBlock)
          blockchainState = ConsensusResolver.receiveChain(newChain, blockchainState)
        }
      })
      blockchainState = ConsensusResolver.pickMaxValid(blockchainState)
      env.timeProvider.advance(env.slotDurationInMilliseconds)

      slotInEpoch = slotInEpochCalculator.calculate(env.timeProvider)
      genesis = virtualGenesis.computeNewGenesis(blockchainState.fullBlockchain, slotInEpoch, previousGenesis = genesis)
    }
  }
}