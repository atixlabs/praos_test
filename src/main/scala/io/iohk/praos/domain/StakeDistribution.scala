package io.iohk.praos.domain

import io.iohk.praos.crypto.Key
import io.iohk.praos.domain.StakeDistribution.Stake

import scala.collection.immutable.Map

trait StakeDistribution {

  /**
    * Returns the Key's stake
    * @param key Key to get the stake from
    * @return Stake
    */
  def stakeOf(key: Key): Stake

  /**
    * Applies a transaction to the StakeDistribution, ie, move stake
    * @param transaction Transaction to apply
    * @return Updated StakeDistribution
    */
  def applyTransaction(transaction: Transaction): StakeDistribution = {
    import transaction._
    transfer(senderPublicKey, recipientPublicKey, stake)
  }

  /**
    * Transfer stake
    * @param from Source key
    * @param to Receipient key
    * @param stake Stake to transfer
    * @return Updated StakeDistribution
    */
  def transfer(from: Key, to: Key, stake: Stake): StakeDistribution

  /**
    * Calculates TotalStake
    * @return Stake
    */
  def totalStake: Stake

}

object StakeDistribution {
  type Stake = Int
}

case class StakeDistributionImpl(stakeMap: Map[Key, Int]) extends StakeDistribution {
  override def stakeOf(key: Key): Stake = stakeMap.getOrElse(key, 0)

  override def transfer(from: Key, to: Key, stake: Stake): StakeDistribution = {
    val fromStake = stakeOf(from)
    val toStake = stakeOf(to)

    require(fromStake >= stake, s"Sender $from does not have stake $stake to transfer!")

    if(from == to) this
    else {
      val finalFromStake = fromStake - stake
      val finalToStake = toStake + stake
      copy(stakeMap = stakeMap + (from -> finalFromStake) + (to -> finalToStake))
    }
  }

  override def totalStake: Stake = stakeMap.values.sum
}
