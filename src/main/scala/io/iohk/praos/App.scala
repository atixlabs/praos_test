package io.iohk.praos

/*import java.security.SecureRandom

import io.iohk.praos.domain._
import io.iohk.praos.crypto._
import io.iohk.praos.ledger.{ConsensusResolver, LedgerImpl}
import io.iohk.praos.util.TransactionsGenerator*/
import io.iohk.praos.util.Logger

object App extends Logger{

  def main(args: Array[String]): Unit = ??? /*{
    // Setup an Stake Distribution
    val (publicKey1, privateKey1) = keyPairToByteStrings(generateKeyPair(new SecureRandom()))
    val stakeholder1 = Stakeholder(privateKey1, publicKey1)
    val (publicKey2, privateKey2) = keyPairToByteStrings(generateKeyPair(new SecureRandom()))
    val stakeholder2 = Stakeholder(privateKey2, publicKey2)
    val (publicKey3, privateKey3) = keyPairToByteStrings(generateKeyPair(new SecureRandom()))
    val stakeholder3 = Stakeholder(privateKey3, publicKey3)
    val stakeHolders = List(stakeholder1, stakeholder2, stakeholder3)
    val stakeDistribution = StakeDistributionImpl(Map(stakeholder1.publicKey -> 5, stakeholder2.publicKey -> 6, stakeholder3.publicKey -> 4))
    // Setup Environment
    val env: Environment = Environment(
      stakeDistribution,
      epochLength = 30,
      k = 3,
      slotDurationInMilliseconds = 3000,
      timeProvider = new TimeProvider(initialTime = 0),
      activeSlotCoefficient = 0.70,
      initialNonce = generateNewRandomValue()
    )
    // Setup Domain objects
    val slotInEpochCalculator = SlotInEpochCalculator(
      epochLength = env.epochLength,
      slotDurationInMilliseconds = env.slotDurationInMilliseconds
    )
    val vrf = VerifiableRandomFunctionStubImpl
    val blockFactory = BlockFactory(signer = SignerImpl, vrf)
    val virtualGenesis = VirtualGenesisImpl(env.epochLength, env.k)
    val ledger = LedgerImpl(ConsensusResolver)
    // Setup initial App state
    var blockchainState = BlockchainState(
      fullBlockchain = List.empty[Block],
      receivedChains = List.empty[Blockchain]
    )
    var genesis = Genesis(env.initialStakeDistribution, env.initialNonce)
    var genesisHistory = GenesisHistoryImpl.appendAt(genesis, 0)
    // Run the protocol for 1 epoch
    (1 to env.epochLength).foreach { _ =>
      val slotInEpoch = slotInEpochCalculator.calculate(env.timeProvider)
      genesis = virtualGenesis.computeGenesis(slotInEpoch, genesisHistory)
      /**
        * @note slotState contains the stakeDistribution and nonce of the current slot
        * For technical reasons we compute these data slot by slot.
        */
      var slotState: Genesis = genesisHistory.getGenesisAt(slotInEpoch.slotNumber - 1)
      stakeHolders.foreach(stakeholder => {
        val isLeaderProof: Option[(RandomValue, VerifiableRandomFunction#VrfProof)] =
          ElectionManager(env.activeSlotCoefficient, vrf)
            .isStakeHolderLeader(stakeholder, genesis, slotInEpoch)
        if (isLeaderProof.isDefined) {
          log.debug(s"[Main] - stakeholder(${stakeholder.publicKey.head}) ELECTED for slot ${slotInEpoch.slotNumber} in epoch ${slotInEpoch.epochNumber}")
          val transactions: List[Transaction] = TransactionsGenerator.generateTxs(
            slotState.genesisDistribution,
            stakeHolders.map(_.publicKey),
            5
          )
          val newBlock: Block = blockFactory.makeBlock(
            slotInEpoch.slotNumber,
            isLeaderProof.get,
            transactions,
            blockchainState.maybeHeadBlockHash,
            stakeholder,
            genesis.genesisNonce)
          val newChain: Blockchain = List(newBlock)
          blockchainState = ledger.receiveChain(blockchainState, newChain)
        } else
          log.debug(s"[Main] - stakeholder(${stakeholder.publicKey.head}) NOT elected for slot ${slotInEpoch.slotNumber} in epoch ${slotInEpoch.epochNumber}")
      })
      /**
        * @note Applies the new transactions from the received blocks that belongs to the largest chain.
        */
      (slotState, blockchainState) = ledger.slotEnd(slotState, blockchainState)
      genesisHistory = genesisHistory.appendAt(slotState, slotInEpoch.slotNumber)
      log.debug(s"[Main] - Blockchain: [${blockchainState.fullBlockchain.map(_.value.slotNumber).mkString("->")}]")
      log.debug(s"[Main] - SLOT ${slotInEpoch.slotNumber} end in epoch ${slotInEpoch.epochNumber}")
      env.timeProvider.advance(env.slotDurationInMilliseconds)
    }
  }*/
}