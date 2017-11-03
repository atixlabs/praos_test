package io.iohk.praos.domain

import io.iohk.praos.crypto.Seed

/**
  * @param k must verify 2k < epochLength
  */
case class Environment(initialStakeDistribution: StakeDistribution,
                       epochLength: Int,
                       k: Int,
                       slotDurationInMilliseconds: Int,
                       timeProvider: TimeProvider,
                       activeSlotCoefficient: Double,
                       initialNonce: Seed)
