package io.iohk.praos

import io.iohk.praos.crypto.Key
import scala.collection.immutable.Map


package object domain {

  /**
    * An amount of stake held by a stakeholder.
    */
  type Stake = Int

  /**
    * A stake distribution. As noted in Section 2.2.1 of Praos Formalization, stakeholders are identified by their
    * public keys.
    */
  type StakeDistribution = Map[Key, Stake]
  val StakeDistribution = Map
}

