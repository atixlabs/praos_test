package io.iohk.praos.domain

import io.iohk.praos.util.Logger


case class VirtualGenesis(k: Int) extends Logger {
  /**
    * Re-think the way we are going to update the epoch
    */
  def computeNewGenesis(blockchain: Blockchain, slotInEpoch: SlotInEpoch, previousGenesis: GenesisBlock): GenesisBlock = {
    import slotInEpoch._
    (slotNumber, firstInEpoch) match {
      case (1, _) => previousGenesis // initialGenesis
      case (_, true) => {
        log.debug(s"[VirtualGenesis] - NEW EPOCH ${slotInEpoch.epochNumber}")
        // TODO: Back 2k blocks and take the stake snapshot and the generate the new genesisNonce
        previousGenesis
      }
      case _ => previousGenesis
    }
  }
}
