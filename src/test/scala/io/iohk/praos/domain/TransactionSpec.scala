package io.iohk.praos.domain

import io.iohk.praos.crypto.Key
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}


/**
  * Unit tests for class [[io.iohk.praos.domain.Transaction]].
  */
class TransactionSpec extends FlatSpec with GivenWhenThen with Matchers {

  // Stakeholder U1
  var publicKey1: Key = akka.util.ByteString("1")

  // Stakeholder U2
  val publicKey2: Key = akka.util.ByteString("2")

  // Stakeholder U3
  val publicKey3: Key = akka.util.ByteString("3")

  // A stake distribution assigning 10 to U1, 2 to U2 and 8 to U3.
  val stakeDistribution = StakeDistribution(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8)

  // A transaction transferring a stake of 3 from U1 to U2.
  val transaction = Transaction(publicKey1, publicKey2, 3)

  // The resulting stake distribution of applying "transaction" to "stakeDistribution".
  val resultingStakeDistribution = applyTransaction(transaction, stakeDistribution)

  "The stakeholder U1 holding a stake of 10" should "have a resulting stake of 7" in {
    When("a transaction transferring a stake of 3 from U1 to U2 is applied")
    resultingStakeDistribution(publicKey1) shouldBe 7
  }

  "The stakeholder U2 holding a stake of 2" should "have a resulting stake of 5" in {
    When("a transaction transferring a stake of 3 from U1 to U2 is applied")
    resultingStakeDistribution(publicKey2) shouldBe 5
  }

  "A well-formed transaction" should "preserve the total stake across stake distributions" in {
    val resultingStakeDistributionTotalStake = resultingStakeDistribution.values.sum
    resultingStakeDistributionTotalStake shouldBe 20
  }
}
