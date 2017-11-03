package io.iohk.praos.crypto

import akka.util.ByteString

import scala.math.abs

trait VerifiableRandomFunction {

  type VrfProof = ByteString

  def prove(privateKey: Key, randomSeed: RandomValue): (RandomValue, VrfProof)

  def verify(publicKey: Key, randomSeed: RandomValue, nonce: (RandomValue, VrfProof)): Boolean
}

object VerifiableRandomFunction {
  val randomLength = 32
}


/**
  * TODO: Fixme!
  * This is a fake implementation that fullfills VerifiableRandomFunction properties
  */
object VerifiableRandomFunctionStubImpl extends VerifiableRandomFunction {

  def prove(privateKey: Key, randomSeed: RandomValue): (RandomValue, VrfProof) =
    (abs(BigInt(PredefinedHasher("MD5").hash(getPublicKeyFromPrivateKey(privateKey) ++ ByteString(randomSeed)).toArray).toInt), getPublicKeyFromPrivateKey(privateKey))

  def verify(publicKey: Key, randomSeed: RandomValue, nonce: (RandomValue, VrfProof)): Boolean =
    (abs(BigInt(PredefinedHasher("MD5").hash(publicKey ++ ByteString(randomSeed)).toArray).toInt), publicKey) == nonce
}
