package io.iohk.praos.domain


case class VirtualGenesis(k: Int) {
  /**
    * Re-think the way we are going to update the epoch
    */
  def computeNewGenesis(blockchain: Blockchain, slotInEpoch: SlotInEpoch, previousGenesis: GenesisBlock): GenesisBlock = {
    import slotInEpoch._
    (slotNumber, firstInEpoch) match {
      case (_, true) => {
        // TODO: Back 2k blocks and take the stake snapshot and the generate the new genesisNonce
        previousGenesis
      }
      case _ => previousGenesis
    }
  }
}
