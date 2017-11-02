package io.iohk.praos.util

import scala.annotation.tailrec
import scala.util.Random
import io.iohk.praos.crypto.Key
import io.iohk.praos.domain.{Stake, StakeDistribution, Transaction, applyTransaction}

object TransactionsGenerator {

  type Stakeholder = (Key, Stake)

  private def chooseRandomStakeholderFrom(stakeDistribution: StakeDistribution): Stakeholder = {
    val stakeDistributionAsSeq = stakeDistribution.toSeq
    val index = Random.nextInt(stakeDistributionAsSeq.length)
    stakeDistributionAsSeq(index)
  }

  private def chooseRandomStakeFrom(stakeUpperBound: Stake): Stake = Random.nextInt(stakeUpperBound)

  /**
    * Given a stake distribution generates a transaction choosing randomly two stakeHolders
    * and a random amount from the sender stakeholder account.
    *
    * @return A tuple with the transaction generated, and the stake distribution with the transaction applied
    */
  def generateTx(stakeDistribution: StakeDistribution): Transaction = {
    val sender = chooseRandomStakeholderFrom(stakeDistribution)
    val recipient = chooseRandomStakeholderFrom(stakeDistribution)
    Transaction(
      senderPublicKey = sender._1,
      recipientPublicKey = recipient._1,
      stake = chooseRandomStakeFrom(stakeUpperBound = sender._2)
    )
  }

  def generateTxs(stakeDistribution: StakeDistribution, n: Int): List[Transaction] = {
    @tailrec
    def rec(stakeDistribution: StakeDistribution, n: Int, acc: List[Transaction]): List[Transaction] = {
      val transaction = generateTx(stakeDistribution)
      if (n == 0) acc
      else {
        val newStakeDistribution = applyTransaction(transaction, stakeDistribution)
        rec(newStakeDistribution, n - 1, transaction :: acc)
      }
    }
    rec(stakeDistribution, n, List.empty[Transaction])
  }
}