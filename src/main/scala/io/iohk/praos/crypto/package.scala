package io.iohk.praos

package object crypto {
  type Key = Array[Byte]
  type KeyPair = (Key, Key)
  type SecureRandom = Int

  // Dummy implementation to simulate a public-private key schema.
  val keyLength = 32
  private def genKey = (s: SecureRandom, length: Int) => new Key(length) map(_ => s.toByte)

  private def XOR(x: Array[Byte], y: Array[Byte]): Array[Byte] =
    (x.toList zip y.toList).map(elements => (elements._1 ^ elements._2).toByte).toArray

  // Post: Return (publicKey, privateKey)
  def generateKeyPair(seed: SecureRandom): KeyPair = {
    (genKey(seed + 1, keyLength), genKey(seed, keyLength))
  }

  def getPublicKeyFromPrivateKey(privateKey: Key): Key = {
    privateKey map(byte => (byte - 1).toByte)
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
    def encryptWith(data: Array[Byte], publicKey: Key): Array[Byte] = XOR(data, publicKey)

    def decryptWith(encryptedData: Array[Byte], privateKey: Key): Option[Array[Byte]] =
      Option(XOR(encryptedData, getPublicKeyFromPrivateKey(privateKey)))
  }

  object SignerImpl extends Signer {
    def signedWith(data: Array[Byte], privateKey: Key): Array[Byte] =
      (privateKey map (byte => (byte + 1).toByte)) ++ data

    def stripSignature(signedData: Array[Byte]): (Key, Array[Byte]) = signedData.splitAt(keyLength)
  }
}
