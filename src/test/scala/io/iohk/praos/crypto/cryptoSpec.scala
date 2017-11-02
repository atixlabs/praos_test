package io.iohk.praos.crypto

import org.scalatest.{FlatSpec, Matchers}
import akka.util.ByteString

class cryptoSpec extends FlatSpec with Matchers {

  trait testSetup {
    val (userPublicKey, userPrivateKey) = generateKeyPair(generateNewRandomValue())
    val cipher: Cipher = CipherStubImpl
    val signer: Signer = SignerStubImpl
    def serialize(data: String): ByteString = ByteString(data)
  }

  it should "get the user public key from its private key" in new testSetup {
    getPublicKeyFromPrivateKey(userPrivateKey) should be (userPublicKey)
  }

  "Cipher" should "encrypt a message using user's pk and then decrypt them using user's sk correctly" in new testSetup {
    val message = serialize("a secret message")
    cipher.decryptWith(cipher.encryptWith(message, userPublicKey), userPrivateKey) should be (Some(message))
  }

  "Cipher" should "NOT decrypt an encrypted message using user's pk" in new testSetup {
    val message = serialize("a secret message")
    cipher.decryptWith(cipher.encryptWith(message, userPublicKey), userPublicKey) shouldNot be (Some(message))
  }

  "Signer" should "sign a message using user's sk and then strip the signed message correctly" in new testSetup {
    val message = serialize("I'm stakeholder Ua")
    signer.stripSignature(signer.signedWith(message, userPrivateKey)) should be (userPublicKey, message)
  }

  "Signer" should "NOT sign a message using user's pk and then strip the signed message correctly" in new testSetup {
    val message = serialize("I'm stakeholder Ua")
    signer.stripSignature(signer.signedWith(message, userPublicKey)) shouldNot be (userPublicKey, message)
  }
}
