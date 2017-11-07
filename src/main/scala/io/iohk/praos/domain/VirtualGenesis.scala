package io.iohk.praos.domain
import io.iohk.praos.util.Logger

trait VirtualGenesis {

  def computeGenesisForEpoch(slotInEpoch: SlotInEpoch, genesisHistory: GenesisHistory): Option[Genesis]
}

case class VirtualGenesisImpl(epochLength: Int, lengthForCommonPrefix: Int) extends VirtualGenesis with Logger {

  override def computeGenesisForEpoch(slotInEpoch: SlotInEpoch, genesisHistory: GenesisHistory): Option[Genesis] = {
    val computeGenesis = (computeGenesisForStaticCase orElse computeGenesisForDynamicCase).lift
    computeGenesis(slotInEpoch, genesisHistory)
  }

  /**
    * The paper refer to the first epoch as the static case of the protocol.
    * @return the initial genesis
    */
  private val computeGenesisForStaticCase: PartialFunction[(SlotInEpoch, GenesisHistory), Genesis] = {
    case (slotInEpoch, genesisHistory) if slotInEpoch.epochNumber == 1 => genesisHistory.getGenesisAt(0).get
  }

  /**
    * The paper refers to the epochs after the static case as the dynamic part of the protocol.
    * @return The genesis from the slot in the position: (R * j) - 2k, R the epoch length, j > 1
    *         the current epoch number, k the length that ensures common prefix,
    *         and 2k the value that ensures the liveness property.
    */
  private val computeGenesisForDynamicCase: PartialFunction[(SlotInEpoch, GenesisHistory), Genesis] = {
    case (slotInEpoch, genesisHistory) if slotInEpoch.epochNumber > 1 => {
      val slotsAtLastEpochStarted = epochLength * (slotInEpoch.epochNumber - 1)
      val transactionConfirmationTime = 2 * lengthForCommonPrefix
      genesisHistory.getGenesisAt(slotsAtLastEpochStarted - transactionConfirmationTime).get
    }
  }
}