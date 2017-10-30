package io.iohk.praos.domain

class SlotInEpochCalculator(epochLength: Int, slotDurationInMilliseconds: Int) {

  def calculateSlotNumber(timeProvider: TimeProvider): Int =
    ((timeProvider.getTime - timeProvider.initialTime) / slotDurationInMilliseconds) + 1

  def calculateEpochNumber(timeProvider: TimeProvider): Int =
    (calculateSlotNumber(timeProvider) - 1) / epochLength + 1

  def isNewEpoch(timeProvider: TimeProvider): Boolean = (calculateSlotNumber(timeProvider) % epochLength) == 1

  def calculate(timeProvider: TimeProvider): SlotInEpoch = SlotInEpoch(
    epochNumber = calculateEpochNumber(timeProvider),
    slotNumber = calculateSlotNumber(timeProvider),
    firstInEpoch = isNewEpoch(timeProvider)
  )
}
