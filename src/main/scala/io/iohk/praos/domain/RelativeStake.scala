package io.iohk.praos.domain

import io.iohk.praos.crypto.PublicKey
import io.iohk.praos.domain.MyTypes.StakeDistribution


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
    *
    * @param publicKey
    * @param stakeDistribution
    * @return
    */
  def apply(publicKey: PublicKey, stakeDistribution: StakeDistribution): RelativeStake = {
    val stakeholderStake = stakeDistribution(publicKey)
    val totalStake = stakeDistribution.mapValues(_.toInt).sum
    RelativeStake(stakeholderStake, totalStake)
  }
}

/**
  * The fraction of stake held by a stakeholder w.r.t. the total stake held by all stakeholders.
  *
  * @constructor creates a relative stake given a fraction of stake.
  * @param fraction the fraction of stake
  */
case class RelativeStake(fraction: Double)
