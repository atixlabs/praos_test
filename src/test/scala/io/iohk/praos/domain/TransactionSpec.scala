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
  val stakeDistribution = StakeDistributionImpl(Map(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8))

  // A transaction transferring a stake of 3 from U1 to U2.
  val transaction = Transaction(publicKey1, publicKey2, 3)

  // The resulting stake distribution of applying "transaction" to "stakeDistribution".
  val resultingStakeDistribution = stakeDistribution.applyTransaction(transaction)

  "Transaction" should "transfer stake when applied" in {
    resultingStakeDistribution.stakeOf(publicKey1) shouldBe 7
    resultingStakeDistribution.stakeOf(publicKey2) shouldBe 5
    resultingStakeDistribution.totalStake shouldBe 20
  }
}
