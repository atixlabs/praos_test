package io.iohk.praos.util

import io.iohk.praos.domain.{StakeDistribution, Transaction, applyTransaction}
import io.iohk.praos.crypto.Key
import org.scalatest.{FlatSpec, Matchers}

class TransactionGeneratorSpec  extends FlatSpec with Matchers {

  trait TestSetup {
    val publicKey1: Key = akka.util.ByteString("1")
    val publicKey2: Key = akka.util.ByteString("2")
    val publicKey3: Key = akka.util.ByteString("3")
    val initialStakeDistribution =  StakeDistribution(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8)
  }

  def checkTxIntegrity(initialStakeDistribution: StakeDistribution, tx: Transaction, finalStakeDistribution: StakeDistribution): Boolean ={
    val positiveStakeForSender = finalStakeDistribution(tx.senderPublicKey) >= 0
    val positiveStakeForRecipient = finalStakeDistribution(tx.recipientPublicKey) >= 0
    val lessOrEqualStakeForSender = finalStakeDistribution(tx.senderPublicKey) == (initialStakeDistribution(tx.senderPublicKey) - tx.stake)
    val greaterOrEqualStakeForRecipient = finalStakeDistribution(tx.recipientPublicKey) == (initialStakeDistribution(tx.recipientPublicKey) + tx.stake)
    val senderEqualToRecipient = tx.senderPublicKey == tx.recipientPublicKey
    positiveStakeForSender && positiveStakeForRecipient && (senderEqualToRecipient || (lessOrEqualStakeForSender && greaterOrEqualStakeForRecipient))
  }

  "TransactionGeneratorSpec" should "generate a valid transaction given a stakeholder distribution" in new TestSetup {
    val tx: Transaction = TransactionsGenerator.generateTx(initialStakeDistribution)
    val newStakeDistribution = applyTransaction(tx, initialStakeDistribution)
    checkTxIntegrity(initialStakeDistribution, tx, newStakeDistribution) shouldBe true
  }

  "TransactionGeneratorSpec" should "generate 5 consecutive transactions given a stakeholder distribution" in new TestSetup {
    val txs: List[Transaction] = TransactionsGenerator.generateTxs(initialStakeDistribution, 5)
    txs.foreach(println(_))
    txs.foldLeft(initialStakeDistribution) { (currentStakeDistribution, tx) => {
      val newStakeDistribution = applyTransaction(tx, currentStakeDistribution)
      checkTxIntegrity(currentStakeDistribution, tx, newStakeDistribution) shouldBe true
      newStakeDistribution
    }}
  }

}
