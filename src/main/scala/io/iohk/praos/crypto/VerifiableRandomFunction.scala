package io.iohk.praos.crypto

import akka.util.ByteString

trait VerifiableRandomFunction {

  type VrfProof = ByteString

  /**
    * @return (randomNonce, vrfProof) This tuple could be verifiable given a public key and randomSeed
    */
  def prove(privateKey: Key, randomSeed: RandomValue): (RandomValue, VrfProof)

  def verify(publicKey: Key, randomSeed: RandomValue, vrfVerifiableValues: (RandomValue, VrfProof)): Boolean
}

object VerifiableRandomFunctionImpl extends VerifiableRandomFunction {

  def prove(privateKey: Key, randomSeed: RandomValue): (RandomValue, VrfProof) = {
    val proof = generateProof(privateKey, randomSeed)
    (
      randomFn(proof),
      proof
    )
  }

  def verify(publicKey: Key, randomSeed: RandomValue, vrfVerifiableValues: (RandomValue, VrfProof)): Boolean = {
    val randomNonce = vrfVerifiableValues._1
    val proof = vrfVerifiableValues._2
    proofBelongsToNonce(proof, randomNonce) && proofBelongsToKeyAndSeed(proof, publicKey, randomSeed)
  }

  private val signer = ECDSASignature
  private val codec = ECDSACodec

  private def proofBelongsToNonce(proof: VrfProof, randomNonce: RandomValue) = randomNonce == randomFn(proof)
  private def proofBelongsToKeyAndSeed(proof: VrfProof, publicKey: Key, randomSeed: RandomValue) = {
    val signature = codec.decodeECDSA(proof.toArray)
    signature.publicKey(randomSeed).contains(publicKey)
  }
  private def randomFn(randomSeed: ByteString): RandomValue = kec256(randomSeed)
  private def generateProof(privateKey: Key, randomSeed: RandomValue): ByteString = {
    val signature = signer.sign(randomSeed.toArray, keyPairFromPrvKey(privateKey.toArray))
    codec.encodeECDSA(signature)
  }
}