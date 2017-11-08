package io.iohk.praos.util

import io.iohk.praos.crypto.Key
import io.iohk.praos.domain.StakeDistribution.Stake
import io.iohk.praos.domain.{StakeDistribution, Transaction}

import scala.annotation.tailrec
import scala.util.Random

object TransactionsGenerator {

  def generateTxs(stakeDistribution: StakeDistribution, stakeholderKeys: Seq[Key], n: Int = 1): List[Transaction] = {
    require(n >= 0, "Number of transactions must be greater or equal to zero")
    @tailrec
    def rec(stakeDistribution: StakeDistribution, n: Int, acc: List[Transaction]): List[Transaction] = {
      if (n == 0) acc
      else {
        val transaction = generateTx(stakeDistribution, stakeholderKeys)
        val newStakeDistribution = stakeDistribution.applyTransaction(transaction)
        rec(newStakeDistribution, n - 1, transaction :: acc)
      }
    }
    rec(stakeDistribution, n, List.empty[Transaction]).reverse
  }

  /**
    * Given a stake distribution generates a transaction choosing randomly two stakeHolders
    * and a random amount from the sender stakeholder account.
    *
    * @return A tuple with the transaction generated, and the stake distribution with the transaction applied
    */
  private def generateTx(stakeDistribution: StakeDistribution, stakeholderKeys: Seq[Key]): Transaction = {
    val sender = pickRandomStakeholder(stakeholderKeys)
    val recipient = pickRandomStakeholder(stakeholderKeys)
    val random = pickRandomStakeFrom(stakeDistribution.stakeOf(sender))
    Transaction(
      senderPublicKey = sender,
      recipientPublicKey = recipient,
      stake = random
    )
  }

  private def pickRandomStakeholder(stakeholderKeys: Seq[Key]): Key =
    stakeholderKeys(Random.nextInt(stakeholderKeys.length))

  private def pickRandomStakeFrom(stake: Stake): Stake = Random.nextInt(stake)
}