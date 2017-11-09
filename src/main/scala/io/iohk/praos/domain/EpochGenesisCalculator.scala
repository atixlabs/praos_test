package io.iohk.praos.domain

trait EpochGenesisCalculator {

  def computeGenesisForEpoch(epochNumber: Int, genesisHistory: GenesisHistory): Option[Genesis]
}

case class EpochGenesisCalculatorImpl(epochLength: Int, lengthForCommonPrefix: Int) extends EpochGenesisCalculator {

  override def computeGenesisForEpoch(epochNumber: Int, genesisHistory: GenesisHistory): Option[Genesis] = {
    val computeGenesis = (computeGenesisForStaticCase orElse computeGenesisForDynamicCase).lift
    computeGenesis(epochNumber, genesisHistory).flatten
  }

  /**
    * The paper refer to the first epoch as the static case of the protocol.
    * @return the initial genesis
    */
  private val computeGenesisForStaticCase: PartialFunction[(Int, GenesisHistory), Option[Genesis]] = {
    case (1, genesisHistory) => genesisHistory.getGenesisAt(0)
  }

  /**
    * The paper refers to the epochs after the static case as the dynamic part of the protocol.
    * @return The genesis from the slot in the position: (R * j) - 2k, R the epoch length, j > 1
    *         the current epoch number, k the length that ensures common prefix,
    *         and 2k the value that ensures the liveness property.
    */
  private val computeGenesisForDynamicCase: PartialFunction[(Int, GenesisHistory), Option[Genesis]] = {
    case (epochNumber, genesisHistory) if epochNumber > 1 => {
      val slotsAtLastEpochStarted = epochLength * (epochNumber - 1)
      val transactionConfirmationTime = 2 * lengthForCommonPrefix
      genesisHistory.getGenesisAt(slotsAtLastEpochStarted - transactionConfirmationTime)
    }
  }
}