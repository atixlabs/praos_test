package io.iohk.praos.crypto

import java.security.SecureRandom
import org.scalatest.{FlatSpec, Matchers}

class cryptoSpec extends FlatSpec with Matchers {

  trait TestSetup {
    val (userPrivateKey, userPublicKey) = keyPairToByteStrings(generateKeyPair(new SecureRandom()))
  }

  it should "get the user public key from its private key" in new TestSetup {
    pubKeyFromPrvKey(userPrivateKey) should be (userPublicKey)
  }
}
