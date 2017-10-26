package io.iohk.praos.domain

import io.iohk.praos.crypto.Key


/**
  * Factory for [[io.iohk.praos.domain.RelativeStake]] instances.
  */
object RelativeStake {

  /**
    * Creates a relative stake given two absolute stakes: The total stake held by a particular stakeholder (denoted by
    * si) and the total stake held by all the stakeholders (denoted by sP). The relative stake is calculated as si/sP.
    *
    * @param stakeholderStake The total stake held by a particular stakeholder.
    * @param totalStake       The total stake held by all the stakeholders.
    */
  def apply(stakeholderStake: Stake, totalStake: Stake): RelativeStake = {
    RelativeStake(stakeholderStake.toFloat / totalStake.toFloat)
  }

  /**
    * Creates a relative stake given a stakeholder's public key and a given stake distribution. The relative stake is
    * calculated as the stake (as indicated in the stake distribution) held by the particular stakeholder identified by
    * the given public key, divided by the total stake held by all the stakeholders (as indicated in the stake
    * distribution).
    *
    * @param publicKey The public key used to identify a particular stakeholder.
    * @param stakeDistribution The stake distribution to be used in the calculations.
    */
  def apply(publicKey: Key, stakeDistribution: StakeDistribution): RelativeStake = {
    val stakeholderStake = stakeDistribution(publicKey)
    val totalStake = stakeDistribution.values.sum
    RelativeStake(stakeholderStake, totalStake)
  }
}

/**
  * The fraction of stake held by a stakeholder w.r.t. the total stake held by all stakeholders.
  *
  * @constructor creates a relative stake given a fraction of stake.
  * @param fraction the fraction of stake
  */
final case class RelativeStake(fraction: Double)
