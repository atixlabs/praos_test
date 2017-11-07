package io.iohk.praos.domain

import io.iohk.praos.crypto.Seed


/**
  * A genesis is used at the very start of the blockchain and at the start of each epoch in order to
  * defined the stake distribution and nonce used to calculate the stakeholder leader selection.
  * Also its used for encapsulate the stake distribution in a specific slot (know as SlotState) and
  * is used for validate if the new transactions are valid for the current slot.
  *
  * @param genesisDistribution The stake distribution to be used.
  * @param genesisNonce        The nonce to be used.
  */
case class Genesis(genesisDistribution: StakeDistribution, genesisNonce: Seed)
