package io.iohk.praos.crypto

import java.security.SecureRandom

import org.scalatest.{FlatSpec, Matchers}
import akka.util.ByteString

class CipherStubImplSpec extends FlatSpec with Matchers {

  trait TestSetup {
    val (userPrivateKey, userPublicKey) = keyPairToByteStrings(generateKeyPair(new SecureRandom()))
    val cipher: Cipher = CipherStubImpl
    val message = serialize("a secret message")
    def serialize(data: String): ByteString = ByteString(data)
  }

  "Cipher" should "encrypt a message using user's pk and then decrypt them using user's sk correctly" in new TestSetup {
    cipher.decryptWith(cipher.encryptWith(message, userPublicKey), userPrivateKey) should be (Some(message))
  }

  "Cipher" should "NOT decrypt an encrypted message using user's pk" in new TestSetup {
    cipher.decryptWith(cipher.encryptWith(message, userPublicKey), userPublicKey) shouldNot be (Some(message))
  }
}
