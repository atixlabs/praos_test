package io.iohk.praos

import akka.util.ByteString

package object crypto {
  type Key = ByteString
  type KeyPair = (Key, Key)
  type SecureRandom = Int

  // Dummy implementation to simulate a public-private key schema.
  val keyLength = 32
  private def genKey = (s: SecureRandom, length: Int) => ByteString(Array.fill(length)(s.toByte))

  private def XOR(x: ByteString, y: ByteString): ByteString =
    ByteString((x zip y).map(elements => (elements._1 ^ elements._2).toByte).toArray)

  /**
    * @return (publicKey, privateKey)
    */
  def generateKeyPair(seed: SecureRandom): KeyPair = {
    (genKey(seed + 1, keyLength), genKey(seed, keyLength))
  }

  def getPublicKeyFromPrivateKey(privateKey: Key): Key = {
    privateKey map(byte => (byte + 1).toByte)
  }

  trait Cipher {
    def encryptWith(data: ByteString, publicKey: Key): ByteString
    def decryptWith(encryptedData: ByteString, privateKey: Key): Option[ByteString]
  }

  trait Signer {
    /**
      * @return signedData
      */
    def signedWith(data: ByteString, privateKey: Key): ByteString

    /**
      * @return (publicKey, data)
      */
    def stripSignature(signedData: ByteString): (Key, ByteString)
  }

  object CipherImpl extends Cipher {
    def encryptWith(data: ByteString, publicKey: Key): ByteString = XOR(data, publicKey)

    def decryptWith(encryptedData: ByteString, privateKey: Key): Option[ByteString] =
      Option(XOR(encryptedData, getPublicKeyFromPrivateKey(privateKey)))
  }

  object SignerImpl extends Signer {
    def signedWith(data: ByteString, privateKey: Key): ByteString =
      getPublicKeyFromPrivateKey(privateKey) ++ data

    def stripSignature(signedData: ByteString): (Key, ByteString) = signedData.splitAt(keyLength)
  }
}
