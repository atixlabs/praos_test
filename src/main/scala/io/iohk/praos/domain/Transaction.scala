package io.iohk.praos.domain

import io.iohk.praos.crypto.Key


/**
  * The transfer of a certain amount of stake from one stakeholder to another.
  *
  * @param senderPublicKey  The public key for the stakeholder sending the stake.
  * @param recipientPublicKey  The public key for the stakeholder receiving the stake.
  * @param stake  The amount of stake to be transferred.
  */
case class Transaction(senderPublicKey: Key, recipientPublicKey: Key, stake: Stake) {

  /**
    * Applies a transaction to an initial stake distribution, yielding a resulting stake distribution.
    *
    * @param stakeDistribution  Initial stake distribution.
    * @return  Resulting stake distribution.
    */
  def apply(stakeDistribution: StakeDistribution): StakeDistribution = {
    // TODO: Handle logic errors (i.e. that the transaction makes sense in the initial distribution)
    val resultingSenderStake = stakeDistribution(senderPublicKey) - stake
    val resultingRecipientStake = stakeDistribution(recipientPublicKey) + stake
    val resultingStakeDistribution = stakeDistribution
      .updated(senderPublicKey, resultingSenderStake)
      .updated(recipientPublicKey, resultingRecipientStake)
    resultingStakeDistribution
  }
}