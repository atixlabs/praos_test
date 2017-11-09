package io.iohk.praos.crypto

import org.scalatest.{FlatSpec, Matchers}
import akka.util.ByteString
import scala.util.Random


class SignatureProviderImplSpec extends FlatSpec with Matchers {

  trait TestSetup {
    val (userPublicKey, userPrivateKey) = generateKeyPair(Random.nextInt())
    val signatureProvider: SignatureProvider = SignatureProviderImpl
    val message = serialize("I'm stakeholder Ua")
    def serialize(data: String): ByteString = ByteString(data)
  }

  "The PK" should "be obtained from a signature when using the SK for signing the message" in new TestSetup {
    signatureProvider.getPublicKeyFromSignature(signatureProvider.getSignature(message, userPrivateKey)) shouldBe
      userPublicKey
  }

  "The PK" should "NOT be obtained from a signature when using the PK for signing the message" in new TestSetup {
    signatureProvider.getPublicKeyFromSignature(signatureProvider.getSignature(message, userPublicKey)) should not be
      userPublicKey
  }
}
