package io.iohk.praos.domain

import io.iohk.praos.crypto.Key
import org.scalatest.{FeatureSpec, GivenWhenThen}

class EpochGenesisCalculatorSpec extends FeatureSpec with GivenWhenThen {

  feature("compute genesis for each slot in epoch") {
    scenario("compute the correct genesis for the first 5 slots") {
      Given("an epoch length of 4, the length that ensure common prefix of 1, and an initial genesis")
      val epochGenesisCalculator: EpochGenesisCalculator = EpochGenesisCalculatorImpl(epochLength = 4, lengthForCommonPrefix = 1)
      val publicKey1: Key = akka.util.ByteString("1")
      val publicKey2: Key = akka.util.ByteString("2")
      val publicKey3: Key = akka.util.ByteString("3")
      val initialGenesis = Genesis(
        genesisDistribution = StakeDistributionImpl(Map(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8)),
        genesisNonce = 321
      )

      When("initial genesis is initialized")
      var genesisHistory: GenesisHistory = GenesisHistoryImpl(Map(0 -> initialGenesis))

      Then("epochGenesisCalculator should compute the initial genesis for every slot in epoch 1")
      val slot1 = SlotInEpoch(epochNumber = 1, slotNumber = 1, firstInEpoch = true)
      assert (epochGenesisCalculator.computeGenesisForEpoch(slot1, genesisHistory).contains(initialGenesis))

      val slot2 = SlotInEpoch(epochNumber = 1, slotNumber = 2, firstInEpoch = false)
      assert (epochGenesisCalculator.computeGenesisForEpoch(slot2, genesisHistory).contains(initialGenesis))

      val slot3 = SlotInEpoch(epochNumber = 1, slotNumber = 3, firstInEpoch = false)
      assert (epochGenesisCalculator.computeGenesisForEpoch(slot3, genesisHistory).contains(initialGenesis))

      val slot4 = SlotInEpoch(epochNumber = 1, slotNumber = 4, firstInEpoch = false)
      assert (epochGenesisCalculator.computeGenesisForEpoch(slot4, genesisHistory).contains(initialGenesis))

      When("the new genesis is initialized")
      val newGenesis = Genesis(
        genesisDistribution = StakeDistributionImpl(Map(publicKey1 -> 5, publicKey2 -> 5, publicKey3 -> 10)),
        genesisNonce = 321
      )
      genesisHistory = genesisHistory.appendAt(newGenesis, 2)

      Then("epochGenesisCalculator should compute the new genesis for every slot in epoch 2")
      val slot5 = SlotInEpoch(epochNumber = 2, slotNumber = 5, firstInEpoch = true)
      assert (epochGenesisCalculator.computeGenesisForEpoch(slot5, genesisHistory).contains(newGenesis))
    }

    scenario("throw an exception if the genesis for the given slot isn't initialized yet") {
      Given("none genesis initialized")
      val epochGenesisCalculator: EpochGenesisCalculator = EpochGenesisCalculatorImpl(epochLength = 4, lengthForCommonPrefix = 1)
      val genesisHistory: GenesisHistory = GenesisHistoryImpl(Map.empty)

      When("epochGenesisCalculator try to compute the genesis for any slot, should be fail")
      val slot1 = SlotInEpoch(epochNumber = 1, slotNumber = 1, firstInEpoch = true)
      intercept[NoSuchElementException] {
        epochGenesisCalculator.computeGenesisForEpoch(slot1, genesisHistory).get
      }

      val slot2 = SlotInEpoch(epochNumber = 1, slotNumber = 2, firstInEpoch = false)
      intercept[NoSuchElementException] {
        epochGenesisCalculator.computeGenesisForEpoch(slot2, genesisHistory).get
      }

      val slot3 = SlotInEpoch(epochNumber = 1, slotNumber = 3, firstInEpoch = false)
      intercept[NoSuchElementException] {
        epochGenesisCalculator.computeGenesisForEpoch(slot3, genesisHistory).get
      }

      val slot4 = SlotInEpoch(epochNumber = 1, slotNumber = 4, firstInEpoch = false)
      intercept[NoSuchElementException] {
        epochGenesisCalculator.computeGenesisForEpoch(slot4, genesisHistory).get
      }

      val slot5 = SlotInEpoch(epochNumber = 2, slotNumber = 5, firstInEpoch = true)
      intercept[NoSuchElementException] {
        epochGenesisCalculator.computeGenesisForEpoch(slot5, genesisHistory).get
      }
    }
  }
}