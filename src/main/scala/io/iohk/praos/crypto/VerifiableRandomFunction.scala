package io.iohk.praos.crypto

import akka.util.ByteString

case class VrfProof(random: RandomValue, proof: ByteString)

trait VerifiableRandomFunction {

  def prove(privateKey: Key, randomSeed: RandomValue): VrfProof

  def verify(publicKey: Key, randomSeed: RandomValue, vrfProof: VrfProof): Boolean
}

/**
  * This is a fake implementation that fullfills VerifiableRandomFunction properties
  */
object VerifiableRandomFunctionStubImpl extends VerifiableRandomFunction {

  def prove(privateKey: Key, randomSeed: RandomValue): VrfProof =
    VrfProof(randomSeed, getPublicKeyFromPrivateKey(privateKey))

  def verify(publicKey: Key, randomSeed: RandomValue, vrfProof: VrfProof): Boolean =
    publicKey == vrfProof.proof && randomSeed == vrfProof.random
}

