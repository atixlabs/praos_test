package io.iohk.praos.domain

import io.iohk.praos.crypto.Seed

case class Environment(initialStakeDistribution: StakeDistribution,
                       epochLength: Int,
                       k: Int,
                       slotDurationInMilliseconds: Int,
                       timeProvider: TimeProvider,
                       activeSlotCoefficient: Double,
                       initialNonce: Seed)
