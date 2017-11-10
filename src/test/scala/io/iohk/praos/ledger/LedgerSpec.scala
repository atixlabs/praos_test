package io.iohk.praos.ledger

import io.iohk.praos.crypto._
import io.iohk.praos.domain._
import org.scalatest.{FlatSpec, Matchers}


/**
  * Unit tests for class [[io.iohk.praos.domain.Ledger]].
  */
class LedgerSpec extends FlatSpec with Matchers {

  trait testSetup {
    var publicKey1: Key = akka.util.ByteString("1")
    val publicKey2: Key = akka.util.ByteString("2")
    val publicKey3: Key = akka.util.ByteString("3")
    val stakeDistribution = StakeDistributionImpl(Map(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8))
    val genesisNonce: Seed = 2
    val initialSlotGenesis = Genesis(stakeDistribution, genesisNonce)
    val initialBlockchainState = BlockchainState(
      fullBlockchain = List.empty[Block],
      receivedChains = List.empty[Blockchain]
    )

    val blockManager = BlockManager(signer = SignerImpl, vrf = VerifiableRandomFunctionStubImpl)
    val ledger: Ledger = LedgerImpl(ConsensusResolver, blockManager)
  }

  /**
    * TODO: Update this test when new block creation using a factory is ready,
    * and change block proof when Block verification is ready.
    */
  "The application of a block to an initial genesis block" should
    "apply all transactions in the block and calculate the new nonce" in new testSetup {

    val transaction1 = Transaction(publicKey1, publicKey2, 3)
    val transaction2 = Transaction(publicKey2, publicKey1, 1)

    val block = Block(
      UnsignedBlock(
        state = None,
        slotNumber = 1,
        data = List(transaction1, transaction2),
        proof = akka.util.ByteString("dummy proof") ,
        nonce = (1, akka.util.ByteString("another dummy proof"))
      ),
      signature = akka.util.ByteString("dummy sign")
    )

    val blockchainStateWithReceivedChain = ledger.receiveChain(initialBlockchainState, List(block))
    val (newSlotState, newBlockchainState) = ledger.slotEnd(initialSlotGenesis, blockchainStateWithReceivedChain)

    val newStakeDistribution = StakeDistributionImpl(Map(publicKey1 -> 8, publicKey2 -> 4, publicKey3 -> 8))
    newSlotState.genesisDistribution shouldEqual newStakeDistribution
    newSlotState.genesisNonce shouldBe combineSeeds(genesisNonce, block.value.nonce._1)
  }
}
