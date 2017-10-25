package main.scala.io.iohk.praos

package object crypto {
  type Key = Array[Byte]
  type KeyPair = (Key, Key)
  type SecureRandom = Int

  // Dummy implementation to simulate a public-private key schema.
  val keyLength = 32
  private def genKey = (s: SecureRandom, length: Int) => new Key(length) map(_ => s.toByte)

  def generateKeyPair(seed: SecureRandom): KeyPair = {
    (genKey(seed + 1, keyLength), genKey(seed, keyLength))
  }

  def getPublicKeyFromPrivateKey(privateKey: Key): Key = {
    privateKey map( byte => (byte - 1).toByte)
  }

  trait Cipher {
    def encryptWith(data: Array[Byte], publicKey: Key): Array[Byte]
    def decryptWith(encryptedData: Array[Byte], privateKey: Key): Option[Array[Byte]]
  }

  trait Signer {
    // Post: Return signedData
    def signedWith(data: Array[Byte], privateKey: Key): Array[Byte]
    // Post: Return (publicKey, data)
    def stripSignature(signedData: Array[Byte]): (Key, Array[Byte])
  }

  object CipherImpl extends Cipher {
    def encryptWith(data: Array[Byte], publicKey: Key): Array[Byte] = {
      // TODO: Make a Dummy implementation
      Array[Byte](0)
    }

    def decryptWith(encryptedData: Array[Byte], privateKey: Key): Option[Array[Byte]] = {
      // TODO: Make a Dummy implementation
      Option(Array[Byte](0))
    }
  }

  object SignerImpl extends Signer {
    def signedWith(data: Array[Byte], privateKey: Key): Array[Byte] = {
      // TODO: Make a Dummy implementation
      Array[Byte](0)
    }

    def stripSignature(signedData: Array[Byte]): (Key, Array[Byte]) = {
      // TODO: Make a Dummy implementation
      (Array[Byte](0), Array[Byte](0))
    }
  }
}
