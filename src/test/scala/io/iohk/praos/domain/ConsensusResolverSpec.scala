package io.iohk.praos.domain

import akka.util.ByteString
import io.iohk.praos.crypto.{VrfProof}
import org.scalatest.{FlatSpec, Matchers}

class ConsensusResolverSpec extends FlatSpec with Matchers {

  def generateDummyBlock(slotNumber: SlotNumber, state: Option[ByteString]) = Block(
    state = state,
    slotNumber = slotNumber,
    data = List.empty,
    proof = VrfProof(random = 0, proof = ByteString.empty),
    nonce = (0, VrfProof(random = 0, proof = ByteString.empty)),
    signature = ByteString("dummy sign")
  )

  "Given an initial empty blockchain and a received chain with a consecutive block, a ConsensusResolver" should
    "pick the received chain as the current blockchain" in {

    val initialState = BlockchainState(
      fullBlockchain = List.empty[Block],
      receivedChains = List.empty[Blockchain])

    val block1 = generateDummyBlock(slotNumber = 1, state = None)
    val newChain = List(block1)

    val newState = ConsensusResolver.receiveChain(newChain, initialState)
    val finalState = ConsensusResolver.pickMaxValid(newState)

    finalState.maybeHeadBlockHash.isDefined shouldEqual true
    finalState.maybeHeadBlockHash.get shouldEqual block1.blockHash
  }

  "Given an initial empty blockchain and a received chain with nonconsecutive block, a ConsensusResolver" should
    "maintain the empty blockchain as the current blockchain" in {

    val initialState = BlockchainState(
      fullBlockchain = List.empty[Block],
      receivedChains = List.empty[Blockchain])

    val block = generateDummyBlock(slotNumber = 1, state = Some(ByteString(1)))
    val newChain = List(block)

    val newState = ConsensusResolver.receiveChain(newChain, initialState)
    val finalState = ConsensusResolver.pickMaxValid(newState)

    finalState.maybeHeadBlockHash.isDefined shouldEqual false
  }

  "Given an initial blockchain with two consecutive blocks, and a received chain with one consecutive block, a ConsensusResolver" should
    "pick as a max valid, the blockchain concatenate with the received chain" in {

    val block1 = generateDummyBlock(slotNumber = 1, state = None)
    val block2 = generateDummyBlock(slotNumber = 2, state = Some(block1.blockHash))

    val initialState = BlockchainState(
      fullBlockchain = List(block1, block2),
      receivedChains = List.empty[Blockchain])

    val block3 =  generateDummyBlock(slotNumber = 3, state = Some(block2.blockHash))
    val newChain = List(block3)

    val newState = ConsensusResolver.receiveChain(newChain, initialState)
    val finalState = ConsensusResolver.pickMaxValid(newState)

    finalState.maybeHeadBlockHash.isDefined shouldEqual true
    finalState.maybeHeadBlockHash.get shouldEqual block3.blockHash
  }

  "Given a initial blockchain with two consecutive blocks, and a received chain with nonconsecutive block, a ConsensusResolver" should
    "pick as a max valid, the initial blockchain" in {

    val block1 = generateDummyBlock(slotNumber = 1, state = None)
    val block2 = generateDummyBlock(slotNumber = 2, state = Some(block1.blockHash))

    val initialState = BlockchainState(
      fullBlockchain = List(block1, block2),
      receivedChains = List.empty[Blockchain])

    val block3 =  generateDummyBlock(slotNumber = 3, state = Some(block2.blockHash))
    val block4 =  generateDummyBlock(slotNumber = 4, state = Some(block3.blockHash))
    val newChain = List(block4)

    val newState = ConsensusResolver.receiveChain(newChain, initialState)
    val finalState = ConsensusResolver.pickMaxValid(newState)

    finalState.maybeHeadBlockHash.isDefined shouldEqual true
    finalState.maybeHeadBlockHash.get shouldEqual block2.blockHash
  }

  "Given a initial blockchain with two consecutive blocks, and two received chain with one consecutive block, a ConsensusResolver" should
    "pick as a max valid, the blockchain concatenate with the first received chain" in {

    val block1 = generateDummyBlock(slotNumber = 1, state = None)
    val block2 = generateDummyBlock(slotNumber = 2, state = Some(block1.blockHash))

    val initialState = BlockchainState(
      fullBlockchain = List(block1, block2),
      receivedChains = List.empty[Blockchain])

    val block3 =  generateDummyBlock(slotNumber = 3, state = Some(block2.blockHash))
    val block4 =  generateDummyBlock(slotNumber = 4, state = Some(block2.blockHash))
    val newChain1 = List(block3)
    val newChain2 = List(block4)

    val updatedState1 = ConsensusResolver.receiveChain(newChain1, initialState)
    val updatedState2 =  ConsensusResolver.receiveChain(newChain2, updatedState1)
    val finalState = ConsensusResolver.pickMaxValid(updatedState2)

    finalState.maybeHeadBlockHash.isDefined shouldEqual true
    finalState.maybeHeadBlockHash.get shouldEqual block3.blockHash
  }
}
