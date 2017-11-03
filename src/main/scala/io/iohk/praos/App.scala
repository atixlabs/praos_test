package io.iohk.praos

import io.iohk.praos.domain._
import io.iohk.praos.crypto.{SignerImpl, VerifiableRandomFunction, VerifiableRandomFunctionStubImpl, generateNewRandomValue}
import io.iohk.praos.util.{TransactionsGenerator, Logger}

object App extends Logger {

  def main(args: Array[String]): Unit = {
    log.debug("[Main] - Start")
    val (publicKey1, privateKey1) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder1 = StakeHolder(privateKey1, publicKey1)
    val (publicKey2, privateKey2) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder2 = StakeHolder(privateKey2, publicKey2)
    val (publicKey3, privateKey3) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder3 = StakeHolder(privateKey3, publicKey3)
    val stakeHolders = List(stakeholder1, stakeholder2, stakeholder3)
    val stakeDistribution = StakeDistribution(
      stakeholder1.publicKey -> 5,
      stakeholder2.publicKey -> 60,
      stakeholder3.publicKey -> 4
    )
    val env: Environment = Environment(
      stakeDistribution,
      epochLength = 20,
      k = 3,
      slotDurationInMilliseconds = 3000,
      timeProvider = new TimeProvider(initialTime = 0),
      activeSlotCoefficient = 0.30,
      initialNonce = generateNewRandomValue()
    )
    log.debug(s"[Main] - Setup Environment: EpochLength: ${env.epochLength}, K: ${env.k}, active slot coefficient: ${env.activeSlotCoefficient}")

    val slotInEpochCalculator = SlotInEpochCalculator(
      epochLength = env.epochLength,
      slotDurationInMilliseconds = env.slotDurationInMilliseconds
    )
    val virtualGenesis = VirtualGenesis(env.k)

	  val vrf = VerifiableRandomFunctionStubImpl
    val blockFactory = BlockFactory(signer = SignerImpl, vrf = vrf)

    var blockchainState = BlockchainState(
      fullBlockchain = List.empty[Block],
      receivedChains = List.empty[Blockchain]
    )
    // Init Protocol
    var genesis = GenesisBlock(env.initialStakeDistribution, env.initialNonce)
    // Run Protocol only one epoch
    (1 to env.epochLength).foreach { _ =>
      val slotInEpoch = slotInEpochCalculator.calculate(env.timeProvider)
      genesis = virtualGenesis.computeNewGenesis(blockchainState.fullBlockchain, slotInEpoch, previousGenesis = genesis)
      stakeHolders.foreach(stakeholder => {
        val isLeaderProof: Option[VerifiableRandomFunction#VrfProof] =
          ElectionManager(env.activeSlotCoefficient, vrf)
            .isStakeHolderLeader(stakeholder, genesis, slotInEpoch)
        if (isLeaderProof.isDefined) {
          log.debug(s"[Main] - stakeholder(${stakeholder.publicKey.head}) ELECTED for slot ${slotInEpoch.slotNumber} in epoch ${slotInEpoch.epochNumber}")
          val transactions = TransactionsGenerator.generateTxs(stakeDistribution, 5)
          val newBlock: Block = blockFactory.makeBlock(
            slotInEpoch.slotNumber,
            isLeaderProof.get,
            transactions,
            blockchainState.maybeHeadBlockHash,
            stakeholder,
            genesis.genesisNonce)
          val newChain: Blockchain = List(newBlock)
          blockchainState = ConsensusResolver.receiveChain(newChain, blockchainState)
        } else
          log.debug(s"[Main] - stakeholder(${stakeholder.publicKey.head}) NOT elected for slot ${slotInEpoch.slotNumber} in epoch ${slotInEpoch.epochNumber}")
      })
      blockchainState = ConsensusResolver.pickMaxValid(blockchainState)
      log.debug(s"[Main] - Blockchain: [${blockchainState.fullBlockchain.map(_.value.slotNumber).mkString("->")}]")
      log.debug(s"[Main] - SLOT ${slotInEpoch.slotNumber} end in epoch ${slotInEpoch.epochNumber}")
      env.timeProvider.advance(env.slotDurationInMilliseconds)
    }
  }
}