package io.iohk.praos.domain

case class BlockchainManager() {

  def receiveChain(newChain: Blockchain, state: BlockchainState): BlockchainState = {
    BlockchainState(state.blockchain, state.maybeHeadBlockHash, state.receivedChains :+ newChain)
  }

  /**
    * Expected behaviour:
    * a = [] and b = [x1] result of join(a,b)= [x1]
    * a = [x1, x2, x3] and b = [x3', x4, x5] result of join(a,b) = [x1, x2, x3', x4, x5]
    * a = [x1, x2, x3] and b = [x4, x5] result of join(a,b) = [x1, x2, x3, x4, x5]
    * a = [x1, x2, x3] and b = [x5, x6] result of join(a,b) join(a,b) = []
    * a = [x1, x2, x3] and b = [x3] the result of join(a,b) join(a,b) = []
    *
    * Currently we only consider:
    * a = [] and b = [x1] result of join(a,b)= [x1]
    * a = [x1, x2] and b = [x3] result of join(a,b)= [x1, x2, x3]
    */
  private def join(baseBlockchain: Blockchain, possibleExtension: Blockchain): Option[Blockchain] = {
    possibleExtension match {
      case Nil => Some(baseBlockchain)
      case possibleExtensionHead :: _ if baseBlockchain.lastOption.map(_.blockHash) == possibleExtensionHead.state =>
        Some(baseBlockchain ++ possibleExtension)
      case _ => None
    }
  }

  /**
    * @return The new BlockchainState, with the current max valid blockchain and without received chains.
    */
  def pickMaxValid(state: BlockchainState): BlockchainState = {
    val validBlockChains: List[Option[Blockchain]] = state.receivedChains
      .map(receivedChain => join(state.blockchain, receivedChain))
    val maxBlockchain = validBlockChains.foldLeft(state.blockchain){ (currentMaxBlockchain, maybeBlockchain) =>
      if (maybeBlockchain.isDefined && maybeBlockchain.get.length > currentMaxBlockchain.length) maybeBlockchain.get
      else currentMaxBlockchain
    }
    BlockchainState(maxBlockchain, maxBlockchain.lastOption.map(_.blockHash), List.empty[Blockchain])
  }
}
