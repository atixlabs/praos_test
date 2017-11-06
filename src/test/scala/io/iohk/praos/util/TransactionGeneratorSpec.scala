package io.iohk.praos.util

import io.iohk.praos.domain.{StakeDistribution, StakeDistributionImpl, Transaction}
import io.iohk.praos.crypto.Key
import org.scalatest.{FlatSpec, Matchers}

class TransactionGeneratorSpec  extends FlatSpec with Matchers {

  trait TestSetup {
    val publicKey1: Key = akka.util.ByteString("1")
    val publicKey2: Key = akka.util.ByteString("2")
    val publicKey3: Key = akka.util.ByteString("3")
    val initialStakeMap = Map(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8)
    val initialStakeDistribution = StakeDistributionImpl(initialStakeMap)
  }

  "TransactionGeneratorSpec" should "generate a valid transaction given a stakeholder distribution" in new TestSetup {
    val tx: Transaction = TransactionsGenerator.generateTxs(initialStakeDistribution, initialStakeMap.keys.toSeq).head
    val newStakeDistribution = initialStakeDistribution.applyTransaction(tx)
    checkTxIntegrity(initialStakeDistribution, tx, newStakeDistribution) shouldBe true
  }

  "TransactionGeneratorSpec" should "generate 5 consecutive transactions given a stakeholder distribution" in new TestSetup {
    val txs: List[Transaction] = TransactionsGenerator.generateTxs(initialStakeDistribution, initialStakeMap.keys.toSeq, 5)
    txs.foldLeft(initialStakeDistribution: StakeDistribution) { (currentStakeDistribution, tx) => {
      val newStakeDistribution = currentStakeDistribution.applyTransaction(tx)
      checkTxIntegrity(currentStakeDistribution, tx, newStakeDistribution) shouldBe true
      newStakeDistribution
    }}
  }

  def checkTxIntegrity(initialStakeDistribution: StakeDistribution, tx: Transaction, finalStakeDistribution: StakeDistribution): Boolean ={
    val positiveStakeForSender = finalStakeDistribution.stakeOf(tx.senderPublicKey) >= 0
    val positiveStakeForRecipient = finalStakeDistribution.stakeOf(tx.recipientPublicKey) >= 0
    val lessOrEqualStakeForSender = finalStakeDistribution.stakeOf(tx.senderPublicKey) == (initialStakeDistribution.stakeOf(tx.senderPublicKey) - tx.stake)
    val greaterOrEqualStakeForRecipient = finalStakeDistribution.stakeOf(tx.recipientPublicKey) == (initialStakeDistribution.stakeOf(tx.recipientPublicKey) + tx.stake)
    val senderEqualToRecipient = tx.senderPublicKey == tx.recipientPublicKey
    positiveStakeForSender && positiveStakeForRecipient && (senderEqualToRecipient || (lessOrEqualStakeForSender && greaterOrEqualStakeForRecipient))
  }
}
