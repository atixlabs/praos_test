package io.iohk.praos.beacon

import org.scalatest.{FlatSpec, Matchers}

class SlotInEpochCalculatorSpec extends FlatSpec with Matchers {

  trait TestSetup {
    val epochLength = 2
    val slotDurationInMilliseconds = 3000
    val initialTime = 100
  }

  "SlotInEpochCalculatorSpec" should "iterate in slots on an entire epoch and then increase epoch" in new TestSetup {
    val timeProvider = new TimeProvider(initialTime)
    val slotInEpochCalculator = new SlotInEpochCalculator(epochLength, slotDurationInMilliseconds)
    slotInEpochCalculator.calculate(timeProvider) shouldEqual SlotInEpoch(
      epochNumber = 1,
      slotNumber = 1,
      firstInEpoch = true
    )
    timeProvider.advance(slotDurationInMilliseconds)
    slotInEpochCalculator.calculate(timeProvider) shouldEqual SlotInEpoch(
      epochNumber = 1,
      slotNumber = 2,
      firstInEpoch = false
    )
    timeProvider.advance(slotDurationInMilliseconds)
    slotInEpochCalculator.calculate(timeProvider) shouldEqual SlotInEpoch(
      epochNumber = 2,
      slotNumber = 3,
      firstInEpoch = true
    )
  }
}
