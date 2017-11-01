package io.iohk.praos.domain

import io.iohk.praos.crypto.Key


/**
  * Calculator for [[io.iohk.praos.domain.RelativeStake]] instances.
  */
object RelativeStakeCalculator {

  /**
    * Calculates a relative stake given two absolute stakes: The total stake held by a particular stakeholder (denoted
    * by si) and the total stake held by all the stakeholders (denoted by sP). The relative stake is calculated as
    * si/sP.
    *
    * @param stakeholderStake The total stake held by a particular stakeholder.
    * @param totalStake The total stake held by all the stakeholders.
    */
  def calculate(stakeholderStake: Stake, totalStake: Stake): RelativeStake = {
    require(totalStake > 0, "The stake held by all stakeholders should be > 0")
    require(totalStake >= stakeholderStake, "No stakeholder should have more stake than the total stake")
    stakeholderStake.toFloat / totalStake.toFloat
  }

  /**
    * Calculates a relative stake given a stakeholder's public key and a given stake distribution. The relative stake is
    * calculated as the stake (as indicated in the stake distribution) held by the particular stakeholder identified by
    * the given public key, divided by the total stake held by all the stakeholders (as indicated in the stake
    * distribution).
    *
    * @param publicKey The public key used to identify a particular stakeholder.
    * @param stakeDistribution The stake distribution to be used in the calculations.
    */
  def calculate(publicKey: Key, stakeDistribution: StakeDistribution): RelativeStake = {
    require(stakeDistribution.contains(publicKey), "No such stakeholder in the stake distribution")
    val stakeholderStake = stakeDistribution(publicKey)
    val totalStake = stakeDistribution.values.sum
    RelativeStakeCalculator.calculate(stakeholderStake, totalStake)
  }
}

