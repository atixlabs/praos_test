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

  "A signature for a message" should "be split into the PK and message when using the SK" in new TestSetup {
    signatureProvider.splitSignature(signatureProvider.getSignature(message, userPrivateKey)) should be
      (userPublicKey, message)
  }

  "A signature for a message" should "NOT be split into the PK and message when using the PK" in new TestSetup {
    signatureProvider.splitSignature(signatureProvider.getSignature(message, userPublicKey)) shouldNot be
      (userPublicKey, message)
  }
}
