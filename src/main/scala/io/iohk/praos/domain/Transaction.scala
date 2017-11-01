package io.iohk.praos.domain

import io.iohk.praos.crypto.Key


/**
  * The transfer of a certain amount of stake from one stakeholder to another.
  *
  * @param senderPublicKey  The public key for the stakeholder sending the stake.
  * @param recipientPublicKey  The public key for the stakeholder receiving the stake.
  * @param stake  The amount of stake to be transferred.
  */
case class Transaction(senderPublicKey: Key, recipientPublicKey: Key, stake: Stake)
