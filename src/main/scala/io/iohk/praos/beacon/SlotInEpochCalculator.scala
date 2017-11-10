package io.iohk.praos.beacon

/**
  * FIX ME!
  */
case class SlotInEpochCalculator(
  initialTimeInMilliseconds: Long,
  epochLength: Long,
  slotDurationInMilliseconds: Long
  ) {

  def calculate(timeInMilliseconds: Long): Option[SlotInEpoch] = {

    calculateEpochNumber(timeInMilliseconds)

  } SlotInEpoch(
    epochNumber = calculateEpochNumber(timeInMilliseconds),
    slotNumber = calculateSlotNumber(timeInMilliseconds),
    firstInEpoch = isNewEpoch(timeInMilliseconds)
  )

  /**
    * The first Slot is started from 1
    */
  private def calculateSlotNumber(timeInMilliseconds: Long): Int = {
    timeInMilliseconds -
  }
    ((timeInMilliseconds - initialTimeInMilliseconds) / slotDurationInMilliseconds) + 1

  /**
    * The first Epoch is started from 1
    */
  private def calculateEpochNumber(slotNumber: Long): Long =
    (slotNumber - 1) / epochLength + 1

  /**
    * An Epoch concur during R slots (epochLength). So if the current Slot reach the R slots (the slot R included)
    * we considered that a new Epoch begins.
    */
  private def isNewEpoch(slotNumber: Long): Boolean = (slotNumber % epochLength) == 1

}
