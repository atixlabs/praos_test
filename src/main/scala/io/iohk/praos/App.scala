package io.iohk.praos

import io.iohk.praos.beacon.{SlotInEpochCalculator, TimeProvider}
import io.iohk.praos.domain._
import io.iohk.praos.crypto._
import io.iohk.praos.ledger.{ConsensusResolver, LedgerImpl}
import io.iohk.praos.util.TransactionsGenerator
import io.iohk.praos.util.Logger

object App extends Logger {

  // scalastyle:off method.length
  def main(args: Array[String]): Unit = {
    // Setup an Stake Distribution
    val (publicKey1, privateKey1) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder1 = Stakeholder(privateKey1, publicKey1)
    val (publicKey2, privateKey2) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder2 = Stakeholder(privateKey2, publicKey2)
    val (publicKey3, privateKey3) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder3 = Stakeholder(privateKey3, publicKey3)
    val stakeHolders = List(stakeholder1, stakeholder2, stakeholder3)
    val stakeDistribution = StakeDistributionImpl(Map(stakeholder1.publicKey -> 5, stakeholder2.publicKey -> 6, stakeholder3.publicKey -> 4))
    // Setup Environment
    val env: Environment = Environment(
      stakeDistribution,
      epochLength = 30,
      lengthForCommonPrefix = 3,
      slotDurationInMilliseconds = 3000,
      timeProvider = new TimeProvider(initialTime = 0),
      activeSlotCoefficient = 0.20,
      initialNonce = generateNewRandomValue()
    )
    // Setup Domain objects
    val slotInEpochCalculator = SlotInEpochCalculator(
      epochLength = env.epochLength,
      slotDurationInMilliseconds = env.slotDurationInMilliseconds
    )
    val vrf = VerifiableRandomFunctionStubImpl
    val blockFactory = BlockFactory(signer = SignerImpl, vrf)
    val epochGenesisCalculator: EpochGenesisCalculator = EpochGenesisCalculatorImpl(env.epochLength, env.lengthForCommonPrefix)
    val ledger = LedgerImpl(ConsensusResolver)
    // Setup initial App state
    var blockchainState = BlockchainState(
      fullBlockchain = List.empty[Block],
      receivedChains = List.empty[Blockchain]
    )
    var genesisHistory: GenesisHistory = GenesisHistoryImpl(
      Map(0 -> Genesis(env.initialStakeDistribution, env.initialNonce))
    )
    // Run the protocol for 3 epoch
    (1 to env.epochLength * 3).foreach { _ =>
      val slotInEpoch = slotInEpochCalculator.calculate(env.timeProvider)
      val genesis: Genesis = epochGenesisCalculator.computeGenesisForEpoch(slotInEpoch.epochNumber, genesisHistory).getOrElse(
        throw new IllegalStateException("Can not compute a genesis")
      )
      /**
        * @note slotState contains the stakeDistribution and nonce of the current slot
        *       For technical reasons we compute these data slot by slot.
        */
      val currentSlotState: Genesis = genesisHistory.getGenesisAt(slotInEpoch.slotNumber - 1).getOrElse(
        throw new IllegalStateException(s"Can not get slot state for slot ${slotInEpoch.slotNumber}")
      )
      stakeHolders.foreach(stakeholder => {
        ElectionManager(env.activeSlotCoefficient, vrf).isStakeHolderLeader(stakeholder, genesis, slotInEpoch) match {
          case Some((vrfRandomNonce, vrfProof)) => {
            log.debug(s"[Main] - stakeholder(${stakeholder.publicKey.head}) ELECTED " +
              s"for slot ${slotInEpoch.slotNumber} in epoch ${slotInEpoch.epochNumber}")
            val transactions: List[Transaction] = TransactionsGenerator.generateTxs(
              currentSlotState.genesisDistribution,
              stakeHolders.map(_.publicKey),
              5)
            val newBlock: Block = blockFactory.makeBlock(
              slotInEpoch.slotNumber,
              vrfProof,
              transactions,
              blockchainState.maybeHeadBlockHash,
              stakeholder,
              genesis.genesisNonce)
            val newChain: Blockchain = List(newBlock)
            blockchainState = ledger.receiveChain(blockchainState, newChain)
          }
          case None => log.debug(
            s"[Main] - stakeholder(${stakeholder.publicKey.head}) NOT elected " +
              s"for slot ${slotInEpoch.slotNumber} in epoch ${slotInEpoch.epochNumber}"
          )
        }
      })
      /**
        * @note Applies the new transactions from the received blocks that belongs to the largest chain.
        */
      val (newSlotState, newBlockchainState) = ledger.slotEnd(currentSlotState, blockchainState)
      blockchainState = newBlockchainState
      genesisHistory = genesisHistory.appendAt(newSlotState, slotInEpoch.slotNumber)
      log.debug(s"[Main] - Blockchain: [${blockchainState.fullBlockchain.map(_.value.slotNumber).mkString("->")}]")
      log.debug(s"[Main] - SLOT ${slotInEpoch.slotNumber} end in epoch ${slotInEpoch.epochNumber}")
      env.timeProvider.advance(env.slotDurationInMilliseconds)
    }
  }
}
