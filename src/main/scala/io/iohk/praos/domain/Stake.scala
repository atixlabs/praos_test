package io.iohk.praos.domain


/** Implicit conversions for [[io.iohk.praos.domain.Stake]] instances. */
object Stake {
  implicit def toInt(s: Stake) = s.amount
}


/**
  * An amount of stake held by a stakeholder.
  *
  * @constructor creates a stake given an amount.
  * @param amount the stake's amount
  */
case class Stake(amount: Int)
