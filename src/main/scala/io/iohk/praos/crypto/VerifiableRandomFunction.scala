package io.iohk.praos.crypto

import akka.util.ByteString
import scala.math.abs

trait VerifiableRandomFunction {

  type VrfProof = ByteString

  /**
    * @return (nonce, vrfProof)
    */
  def prove(privateKey: Key, randomSeed: RandomValue): (RandomValue, VrfProof)

  def verify(publicKey: Key, randomSeed: RandomValue, vrfVerifiableValues: (RandomValue, VrfProof)): Boolean
}

/**
  * VrfProof contains a fresh random value generated using the user public key, a random seed and
  * VerifiableRandomFunction.prove method. And also contains the proof, that will be used later,
  * with the user public key, for verify that the random value was generated by him.
  */

object VrfProof {
  // TODO: When the VrfProof be well defined, change to the true random length
  val randomLength = 32
}

/**
  * This is a fake implementation that fullfills VerifiableRandomFunction properties
  */
object VerifiableRandomFunctionStubImpl extends VerifiableRandomFunction {

  private val hasher = PredefinedHasher("MD5")

  /**
    * @return (randomNonce, vrfProof) This tuple could be verifiable given a public key and randomSeed
    */
  def prove(privateKey: Key, randomSeed: RandomValue): (RandomValue, VrfProof) = {
    val publicKey = getPublicKeyFromPrivateKey(privateKey)
    (
      abs(BigInt(hasher.hash(publicKey ++ ByteString(randomSeed)).toArray).toInt),
      publicKey
    )
  }

  def verify(publicKey: Key, randomSeed: RandomValue, vrfVerifiableValues: (RandomValue, VrfProof)): Boolean = {
    val (randomNonce, vrfProof) = vrfVerifiableValues
    randomNonce == abs(BigInt(hasher.hash(publicKey ++ ByteString(randomSeed)).toArray).toInt) && vrfProof == publicKey
  }
}
