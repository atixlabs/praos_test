package io.iohk.praos.crypto

import org.scalatest.{FlatSpec, Matchers}
import akka.util.ByteString
import scala.util.Random


class CipherStubImplSpec extends FlatSpec with Matchers {

  trait TestSetup {
    val (userPublicKey, userPrivateKey) = generateKeyPair(Random.nextInt())
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
