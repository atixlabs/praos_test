package io.iohk.praos.domain

import io.iohk.praos.crypto.Key
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}


/**
  * Unit tests for class [[io.iohk.praos.domain.Transaction]].
  */
class TransactionSpec extends FlatSpec with GivenWhenThen with Matchers {

  trait TestSetup {
    // Stakeholder U1
    var publicKey1: Key = akka.util.ByteString("1")

    // Stakeholder U2
    val publicKey2: Key = akka.util.ByteString("2")

    // Stakeholder U3
    val publicKey3: Key = akka.util.ByteString("3")

    // A stake distribution assigning 10 to U1, 2 to U2 and 8 to U3.
    val stakeDistribution = StakeDistributionImpl(Map(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8))

    // A transaction transferring a stake of 3 from U1 to U2.
    val transaction1 = Transaction(publicKey1, publicKey2, 3)

    // A transaction transferring a stake of 3 from U1 to U1 (a "self" transaction).
    val transaction2 = Transaction(publicKey1, publicKey1, 3)
  }

  "The stakeholder U1 holding a stake of 10" should "have a resulting stake of 7" in new TestSetup {
    When("a transaction transferring a stake of 3 from U1 to U2 is applied")
    val resultingStakeDistribution = stakeDistribution.applyTransaction(transaction1)
    resultingStakeDistribution.stakeOf(publicKey1) shouldBe 7
  }

  "The stakeholder U2 holding a stake of 2" should "have a resulting stake of 5" in new TestSetup {
    When("a transaction transferring a stake of 3 from U1 to U2 is applied")
    val resultingStakeDistribution = stakeDistribution.applyTransaction(transaction1)
    resultingStakeDistribution.stakeOf(publicKey2) shouldBe 5
  }

  "A well-formed transaction" should "preserve the total stake across stake distributions" in new TestSetup {
    val resultingStakeDistribution = stakeDistribution.applyTransaction(transaction1)
    resultingStakeDistribution.totalStake shouldBe 20
  }

  "A \"self\" transaction" should "not modify a stake distributions" in new TestSetup {
    val resultingStakeDistribution = stakeDistribution.applyTransaction(transaction2)
    resultingStakeDistribution shouldEqual stakeDistribution
  }
}
