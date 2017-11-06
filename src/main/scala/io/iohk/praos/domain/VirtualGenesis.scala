package io.iohk.praos.domain
import io.iohk.praos.util.Logger

trait VirtualGenesis {

  def computeGenesisForEpoch(slotInEpoch: SlotInEpoch, genesisHistory: GenesisHistory): Genesis
}

case class VirtualGenesisImpl(k: Int) extends VirtualGenesis with Logger {
  /**
    * @return The genesis from the slot in the position number of epochs - 2k
    */
  override def computeGenesisForEpoch(slotInEpoch: SlotInEpoch, genesisHistory: GenesisHistory): Option[Genesis] = {
    import slotInEpoch._
    (slotNumber, firstInEpoch) match {
      case (1, _) => genesisHistory.getGenesisAt(0)
      case (_, true) => {
        log.debug(s"[VirtualGenesis] - NEW EPOCH ${slotInEpoch.epochNumber}")
        genesisHistory.getGenesisAt(slotNumber - 2 * k)
      }
      // TODO: get the genesis for the current epoch
      case _ => None
    }
  }
}