package io.iohk.praos.ledger

import io.iohk.praos.domain.{Block, Blockchain, BlockchainState}

trait ConsensusResolver {
  def pickMaxValid(state: BlockchainState): BlockchainState
}


object ConsensusResolver extends ConsensusResolver {

  /**
    * TODO: Consider cases with possible pending validation of the txs of the blocks.
    * @return The new BlockchainState, with the current max valid blockchain and without received chains.
    */
  def pickMaxValid(state: BlockchainState): BlockchainState = {
    val validBlockChains: List[Option[Blockchain]] =
      state.receivedChains.map(receivedChain => join(state.fullBlockchain, receivedChain))

    val maxBlockchain = validBlockChains.foldLeft(state.fullBlockchain) {
      case (currentMaxBlockchain, Some(maybeBlockchain)) if maybeBlockchain.length > currentMaxBlockchain.length => maybeBlockchain
      case (currentMaxBlockchain, _) => currentMaxBlockchain
    }
    BlockchainState(maxBlockchain, List.empty[Blockchain])
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
      case possibleExtensionHead :: _ if baseBlockchain.lastOption.map(Block.hashValue(_)) == possibleExtensionHead.value.state =>
        Some(baseBlockchain ++ possibleExtension)
      case _ => None
    }
  }

}
