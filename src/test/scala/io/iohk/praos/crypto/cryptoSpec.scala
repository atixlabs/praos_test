package io.iohk.praos.crypto

import org.scalatest.{FlatSpec, Matchers}
import scala.util.Random


class cryptoSpec extends FlatSpec with Matchers {

  trait TestSetup {
    val (userPublicKey, userPrivateKey) = generateKeyPair(Random.nextInt())
  }

  it should "get the user public key from its private key" in new TestSetup {
    getPublicKeyFromPrivateKey(userPrivateKey) should be (userPublicKey)
  }
}
