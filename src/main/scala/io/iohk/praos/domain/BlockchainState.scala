package io.iohk.praos.domain

import akka.util.ByteString

/**
  * @param fullBlockchain: Usually called blockchain, is the longest chain.
  * @param receivedChains: Maintains the list of blockchains received in the epoch
  */
case class BlockchainState(fullBlockchain: Blockchain, receivedChains: List[Blockchain]) {
  lazy val maybeHeadBlockHash: Option[ByteString] = fullBlockchain.lastOption.map(_.blockHash)
}

object BlockchainState {
  /**
    * TODO: Verify the Integrity of the new chain, and the integrity of the blocks that make its up
    */
  def receiveChain(state: BlockchainState, newChain: Blockchain): BlockchainState = {
    require(newChain.nonEmpty, "New Chain should not be empty")
    state.copy(receivedChains = newChain +: state.receivedChains)
  }
}


