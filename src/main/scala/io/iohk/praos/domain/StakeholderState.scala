package io.iohk.praos.domain

import io.iohk.praos.crypto.{Hasher, Key}


object StakeholderState {
  // This function corresponds to the initStakeholderStake function in the Praos Formalization.
  def initialize(privateKey: Key, genesisBlock: GenesisBlock): StakeholderState =
    StakeholderState(privateKey, genesisBlock, genesisBlock, Blockchain(), None, List.empty)
}

case class StakeholderState(
   privateKey     : Key,
   genesisStart   : GenesisBlock,
   genesisCurrent : GenesisBlock,
   blockchain     : Blockchain,
   state          : Option[Hasher#Digest],
   receivedChains : List[Blockchain])