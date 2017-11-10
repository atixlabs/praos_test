package io.iohk.praos.ledger

import akka.util.ByteString
import io.iohk.praos.domain._
import org.scalatest.{FlatSpec, Matchers}

class ConsensusResolverSpec extends FlatSpec with Matchers {

  "Given an initial empty blockchain and a received chain with a consecutive block, a ConsensusResolver" should
    "pick the received chain as the current blockchain" in {

    val initialState = BlockchainState(fullBlockchain = List.empty[Block], receivedChains = List.empty[Blockchain])

    val block1 = generateDummyBlock(slotNumber = 1, state = None)
    val newChain = List(block1)

    val newState = BlockchainState.receiveChain(initialState, newChain)
    val finalState = ConsensusResolver.pickMaxValid(newState)

    finalState.maybeHeadBlockHash.isDefined shouldEqual true
    finalState.maybeHeadBlockHash.get shouldEqual Block.hashValue(block1)
  }

  "Given an initial empty blockchain and a received chain with nonconsecutive block, a ConsensusResolver" should
    "maintain the empty blockchain as the current blockchain" in {

    val initialState = BlockchainState(
      fullBlockchain = List.empty[Block],
      receivedChains = List.empty[Blockchain])

    val block = generateDummyBlock(slotNumber = 1, state = Some(ByteString(1)))
    val newChain = List(block)

    val newState = BlockchainState.receiveChain(initialState, newChain)
    val finalState = ConsensusResolver.pickMaxValid(newState)

    finalState.maybeHeadBlockHash.isDefined shouldEqual false
  }

  "Given an initial blockchain with two consecutive blocks, and a received chain with one consecutive block, a ConsensusResolver" should
    "pick as a max valid, the blockchain concatenate with the received chain" in {

    val block1 = generateDummyBlock(slotNumber = 1, state = None)
    val block2 = generateDummyBlock(slotNumber = 2, state = Some(Block.hashValue(block1)))

    val initialState = BlockchainState(
      fullBlockchain = List(block1, block2),
      receivedChains = List.empty[Blockchain])

    val block3 = generateDummyBlock(slotNumber = 3, state = Some(Block.hashValue(block2)))
    val newChain = List(block3)

    val newState = BlockchainState.receiveChain(initialState, newChain)
    val finalState = ConsensusResolver.pickMaxValid(newState)

    finalState.maybeHeadBlockHash.isDefined shouldEqual true
    finalState.maybeHeadBlockHash.get shouldEqual Block.hashValue(block3)
  }

  "Given a initial blockchain with two consecutive blocks, and a received chain with nonconsecutive block, a ConsensusResolver" should
    "pick as a max valid, the initial blockchain" in {

    val block1 = generateDummyBlock(slotNumber = 1, state = None)
    val block2 = generateDummyBlock(slotNumber = 2, state = Some(Block.hashValue(block1)))

    val initialState = BlockchainState(
      fullBlockchain = List(block1, block2),
      receivedChains = List.empty[Blockchain])

    val block3 = generateDummyBlock(slotNumber = 3, state = Some(Block.hashValue(block2)))
    val block4 = generateDummyBlock(slotNumber = 4, state = Some(Block.hashValue(block3)))
    val newChain = List(block4)

    val newState = BlockchainState.receiveChain(initialState, newChain)
    val finalState = ConsensusResolver.pickMaxValid(newState)

    finalState.maybeHeadBlockHash.isDefined shouldEqual true
    finalState.maybeHeadBlockHash.get shouldEqual Block.hashValue(block2)
  }

  "Given a initial blockchain with two consecutive blocks, and two received chain with one consecutive block, a ConsensusResolver" should
    "pick as a max valid, the blockchain concatenate with the last received chain" in {

    val block1 = generateDummyBlock(slotNumber = 1, state = None)
    val block2 = generateDummyBlock(slotNumber = 2, state = Some(Block.hashValue(block1)))

    val initialState = BlockchainState(
      fullBlockchain = List(block1, block2),
      receivedChains = List.empty[Blockchain])

    val block3 = generateDummyBlock(slotNumber = 3, state = Some(Block.hashValue(block2)))
    val block4 = generateDummyBlock(slotNumber = 4, state = Some(Block.hashValue(block2)))
    val newChain1 = List(block3)
    val newChain2 = List(block4)

    val updatedState1 = BlockchainState.receiveChain(initialState, newChain1)
    val updatedState2 = BlockchainState.receiveChain(updatedState1, newChain2)
    val finalState = ConsensusResolver.pickMaxValid(updatedState2)

    finalState.maybeHeadBlockHash.isDefined shouldEqual true
    finalState.maybeHeadBlockHash.get shouldEqual Block.hashValue(block4)
  }

  def generateDummyBlock(slotNumber: SlotNumber, state: Option[ByteString]) =
  {
    val unsignedBlock = UnsignedBlock(
      state = state,
      slotNumber = slotNumber,
      data = List.empty,
      proof = ByteString.empty,
      nonce = (0, ByteString.empty)
    )
    Block(unsignedBlock = unsignedBlock, signature = ByteString("dummy sign"))
  }
}