package io.iohk.praos.crypto

import akka.util.ByteString


trait VerifiableRandomFunction {

  type VrfProof = ByteString

  def prove(privateKey: Key, randomSeed: RandomValue): (RandomValue, VrfProof)

  def verify(publicKey: Key, randomSeed: RandomValue, nonce: (RandomValue, VrfProof)): Boolean
}

object VerifiableRandomFunction {
  val randomLength = 32
}


/**
  * This is a fake implementation that fullfills VerifiableRandomFunction properties
  */
object VerifiableRandomFunctionStubImpl extends VerifiableRandomFunction {

  def prove(privateKey: Key, randomSeed: RandomValue): (RandomValue, VrfProof) =
    (randomSeed, getPublicKeyFromPrivateKey(privateKey))

  def verify(publicKey: Key, randomSeed: RandomValue, nonce: (RandomValue, VrfProof)): Boolean =
    (randomSeed, publicKey) == nonce
}
