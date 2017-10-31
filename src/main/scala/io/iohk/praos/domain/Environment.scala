package io.iohk.praos.domain

case class Environment(initialStakeDistribution: StakeDistribution,
                       epochLength: Int,
                       slotDurationInMilliseconds: Int,
                       timeProvider: TimeProvider)
