package io.iohk.praos.crypto

import org.scalatest.{FlatSpec, Matchers}
import akka.util.ByteString
import scala.util.Random

class VerifiableRandomFunctionSpec extends FlatSpec with Matchers {

  trait testSetup {
    val (user1PublicKey, user1PrivateKey) = generateKeyPair(Random.nextInt())
    val verifiableRandomFunction: VerifiableRandomFunction = VerifiableRandomFunctionStubImpl
    def serialize(data: String): ByteString = ByteString(data)
  }

  def generateDifferentRandomValue(randomValue: RandomValue): RandomValue = {
    val anotherRandomValue = Random.nextInt()
    if (randomValue == anotherRandomValue) generateDifferentRandomValue(randomValue)
    else anotherRandomValue
  }

  "Verify using the user public key and the same seed" should "be okey" in new testSetup {
    val randomSeed: RandomValue = Random.nextInt()
    val vrfProof: VrfProof = verifiableRandomFunction prove(user1PrivateKey, randomSeed)
    verifiableRandomFunction verify(user1PublicKey, randomSeed, vrfProof) shouldEqual true
  }

  "Verify using the user private key and the same seed" should "not be okey" in new testSetup {
    val randomSeed: RandomValue = Random.nextInt()
    val vrfProof: VrfProof = verifiableRandomFunction prove(user1PrivateKey, randomSeed)
    verifiableRandomFunction verify(user1PrivateKey, randomSeed, vrfProof) shouldEqual false
  }

  "Verify using a public key from a different user and the same seed" should "not be okey" in new testSetup {
    val randomSeed: RandomValue = Random.nextInt()
    val vrfProof: VrfProof = verifiableRandomFunction prove(user1PrivateKey, randomSeed)
    val (user2PublicKey, user2PrivateKey) = generateKeyPair(Random.nextInt())
    verifiableRandomFunction verify(user2PublicKey, randomSeed, vrfProof) shouldEqual false
  }

  "Verify using the user public key and a different seed" should "not be okey" in new testSetup {
    val randomSeed: RandomValue = Random.nextInt()
    val anotherSeed: RandomValue = generateDifferentRandomValue(randomSeed)
    val vrfProof: VrfProof = verifiableRandomFunction prove(user1PrivateKey, randomSeed)
    verifiableRandomFunction verify(user1PublicKey, anotherSeed, vrfProof) shouldEqual false
  }

}
