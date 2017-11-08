package io.iohk.praos.domain

import io.iohk.praos.crypto.Key
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}


/**
  * Unit tests for class [[io.iohk.praos.domain.RelativeStakeCalculator]].
  */
class RelativeStakeCalculatorSpec extends FlatSpec with GivenWhenThen with Matchers {

  // Stakeholder U1
  var publicKey1: Key = akka.util.ByteString("1")

  // Stakeholder U2
  val publicKey2: Key = akka.util.ByteString("2")

  // Stakeholder U3
  val publicKey3: Key = akka.util.ByteString("3")

  // A stake distribution assigning 10 to U1, 2 to U2 and 8 to U3.
  val stakeDistribution = StakeDistributionImpl(Map(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8))

  "A stakeholder holding a stake of 10" should "have a relative stake of 10/20" in {
    Given("a stake distribution with a total stake of 20")
    RelativeStakeCalculator.calculate(publicKey1, stakeDistribution) shouldBe 10f/20
  }

  "A stakeholder holding a stake of 2" should "have a relative stake of 2/20" in {
    Given("a stake distribution with a total stake of 20")
    RelativeStakeCalculator.calculate(publicKey2, stakeDistribution) shouldBe 2f/20
  }

  "A stakeholder holding a stake of 8" should "have a relative stake of 8/20" in {
    Given("a stake distribution with a total stake of 20")
    RelativeStakeCalculator.calculate(publicKey3, stakeDistribution) shouldBe 8f / 20
  }

  "A stakeholder holding all of the stake" should "have a relative stake of 1" in {
    val stakeDistribution = StakeDistributionImpl(Map(publicKey1 -> 10))
    RelativeStakeCalculator.calculate(publicKey1, stakeDistribution) shouldBe 1.0
  }

  "A stakeholder holding no stake" should "have a relative stake of 0" in {
    val stakeDistribution = StakeDistributionImpl(Map(publicKey1 -> 0, publicKey2 -> 10))
    RelativeStakeCalculator.calculate(publicKey1, stakeDistribution) shouldBe 0.0
  }
}
