package io.iohk.praos.domain

case class SlotInEpochCalculator(epochLength: Int, slotDurationInMilliseconds: Int) {

  /**
    * The first Slot is started from 1
    */
  def calculateSlotNumber(timeProvider: TimeProvider): Int =
    ((timeProvider.getTime - timeProvider.initialTime) / slotDurationInMilliseconds) + 1

  /**
    * The first Epoch is started from 1
    */
  def calculateEpochNumber(timeProvider: TimeProvider): Int =
    (calculateSlotNumber(timeProvider) - 1) / epochLength + 1

  /**
    * An Epoch concur during R slots (epochLength). So if the current Slot reach the R slots (the slot R included)
    * we considered that a new Epoch begins.
    */
  def isNewEpoch(timeProvider: TimeProvider): Boolean = (calculateSlotNumber(timeProvider) % epochLength) == 1

  def calculate(timeProvider: TimeProvider): SlotInEpoch = SlotInEpoch(
    epochNumber = calculateEpochNumber(timeProvider),
    slotNumber = calculateSlotNumber(timeProvider),
    firstInEpoch = isNewEpoch(timeProvider)
  )
}
