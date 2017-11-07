package io.iohk.praos

import akka.util.ByteString

import scala.annotation.tailrec
import scala.util.Random
import scala.math.abs

package object crypto {
  type Key = ByteString
  type KeyPair = (Key, Key)
  type RandomValue = Int

  // Dummy implementation to simulate a public-private key schema.
  val keyLength = 32

  private def genKey = (s: RandomValue, length: Int) => ByteString(Array.fill(length)(s.toByte))

  private def XOR(x: ByteString, y: ByteString): ByteString =
    ByteString((x zip y).map(elements => (elements._1 ^ elements._2).toByte).toArray)

  def generateNewRandomValue(): RandomValue = abs(Random.nextInt())

  @tailrec
  def generateDifferentRandomValue(randomValue: RandomValue): RandomValue = {
    val anotherRandomValue = generateNewRandomValue()
    if (randomValue == anotherRandomValue) generateDifferentRandomValue(randomValue)
    else anotherRandomValue
  }

  /**
    * @return (publicKey, privateKey)
    */
  def generateKeyPair(seed: RandomValue): KeyPair = {
    (genKey(seed + 1, keyLength), genKey(seed, keyLength))
  }

  def getPublicKeyFromPrivateKey(privateKey: Key): Key = {
    privateKey map (byte => (byte + 1).toByte)
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

  object CipherStubImpl extends Cipher {
    def encryptWith(data: ByteString, publicKey: Key): ByteString = XOR(data, publicKey)

    def decryptWith(encryptedData: ByteString, privateKey: Key): Option[ByteString] =
      Option(XOR(encryptedData, getPublicKeyFromPrivateKey(privateKey)))
  }

  object SignerStubImpl extends Signer {
    def signedWith(data: ByteString, privateKey: Key): ByteString =
      getPublicKeyFromPrivateKey(privateKey) ++ data

    def stripSignature(signedData: ByteString): (Key, ByteString) = signedData.splitAt(keyLength)
  }

  /**
    * The seed for a PRNG.
    */
  type Seed = Int

  /**
    * Operation used to "combine" seeds. According to the Praos Formalization, it should be associative.
    */
  def combineSeeds(x: Seed, y: Seed): Seed = abs(x + y)

  /**
    * A calculator of hash values.
    */
  trait Hasher {

    /** Message digest. */
    type Digest = ByteString

    /**
      * Hashes a message.
      *
      * @param message  The message to be hashed.
      * @return  The message digest.
      */
    def hash(message: ByteString): Digest
  }

  /**
    * A pre-defined hasher.
    *
    * @param algorithm  A pre-defined algorithm (e.g. "MD5").
    * @note  Precondition: The given algorithm is supported.
    */
  case class PredefinedHasher(algorithm: String) extends Hasher {
    override def hash(message: ByteString): Digest = {
      ByteString(java.security.MessageDigest.getInstance(algorithm).digest(message.toArray))
    }
  }

  type Signature = ByteString
}
