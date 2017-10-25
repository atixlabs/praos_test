package io.iohk.praos.domain

import io.iohk.praos.crypto.PublicKey
import scala.collection.immutable.Map


package object MyTypes {

  /**
    * A stake distribution. As noted in Section 2.2.1, stakeholders are identified by their public keys.
    */
  type StakeDistribution = Map[PublicKey, Stake]
  val StakeDistribution = Map
}

