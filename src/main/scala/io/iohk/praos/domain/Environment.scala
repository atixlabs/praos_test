package io.iohk.praos.domain

import io.iohk.praos.beacon.TimeProvider
import io.iohk.praos.crypto.Seed

case class Environment(initialStakeDistribution: StakeDistribution,
                       epochLength: Int, // The paper refer as R
                       lengthForCommonPrefix: Int, // The paper refer as K
                       slotDurationInMilliseconds: Int,
                       timeProvider: TimeProvider,
                       activeSlotCoefficient: Double,
                       initialNonce: Seed)
