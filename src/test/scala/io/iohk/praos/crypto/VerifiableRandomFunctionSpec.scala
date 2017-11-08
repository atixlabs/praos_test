package io.iohk.praos.crypto

import java.security.SecureRandom

import org.scalatest.{FlatSpec, Matchers}
import akka.util.ByteString

class VerifiableRandomFunctionSpec extends FlatSpec with Matchers {

  trait testSetup {
    val (user1PublicKey, user1PrivateKey) = keyPairToByteStrings(generateKeyPair(new SecureRandom()))
    val verifiableRandomFunction: VerifiableRandomFunction = VerifiableRandomFunctionImpl
    def serialize(data: String): ByteString = ByteString(data)
  }

  "Verify using the user public key and the same seed" should "be okey" in new testSetup {
    val randomSeed: RandomValue = generateNewRandomValue()
    val vrfResult = verifiableRandomFunction prove(user1PrivateKey, randomSeed)
    verifiableRandomFunction verify(user1PublicKey, randomSeed, vrfResult) shouldEqual true
  }

  "Verify using the user private key and the same seed" should "not be okey" in new testSetup {
    val randomSeed: RandomValue = generateNewRandomValue()
    val vrfResult = verifiableRandomFunction prove(user1PrivateKey, randomSeed)
    verifiableRandomFunction verify(user1PrivateKey, randomSeed, vrfResult) shouldEqual false
  }

  "Verify using a public key from a different user and the same seed" should "not be okey" in new testSetup {
    val randomSeed: RandomValue = generateNewRandomValue()
    val vrfResult = verifiableRandomFunction prove(user1PrivateKey, randomSeed)
    val (user2PublicKey, user2PrivateKey) = keyPairToByteStrings(generateKeyPair(new SecureRandom()))
    verifiableRandomFunction verify(user2PublicKey, randomSeed, vrfResult) shouldEqual false
  }

  "Verify using the user public key and a different seed" should "not be okey" in new testSetup {
    val randomSeed: RandomValue = generateNewRandomValue()
    val anotherSeed: RandomValue = generateNewRandomValue()
    val vrfResult = verifiableRandomFunction prove(user1PrivateKey, randomSeed)
    verifiableRandomFunction verify(user1PublicKey, anotherSeed, vrfResult) shouldEqual false
  }

}
