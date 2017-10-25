package io.iohk.praos.crypto


/**
  * A cryptographic key.
  */
sealed abstract class Key {
  val value: BigInt
}

/**
  * A public key.
  * @param value The public key's value.
  */
final case class PublicKey(value: BigInt) extends Key

/**
  * A private key.
  * @param value The private key's value.
  */
final case class PrivateKey(value: BigInt) extends Key
