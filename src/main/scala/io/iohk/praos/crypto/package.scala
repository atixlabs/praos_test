package io.iohk.praos

import akka.util.ByteString


package object crypto {

  type Key = ByteString
  type KeyPair = (Key, Key)
  type RandomValue = Int

  // Dummy implementation to simulate a public-private key schema.
  val keyLength = 32
  def genKey = (s: RandomValue, length: Int) => ByteString(Array.fill(length)(s.toByte))

  def XOR(x: ByteString, y: ByteString): ByteString =
    ByteString((x zip y).map(elements => (elements._1 ^ elements._2).toByte).toArray)

  /**
    * @return (publicKey, privateKey)
    */
  def generateKeyPair(seed: RandomValue): KeyPair = {
    (genKey(seed + 1, keyLength), genKey(seed, keyLength))
  }

  def getPublicKeyFromPrivateKey(privateKey: Key): Key = {
    privateKey map(byte => (byte + 1).toByte)
  }

  /**
    * The seed for a PRNG.
    */
  type Seed = RandomValue

  /**
    * Operation used to "combine" seeds. According to the Praos Formalization, it should be associative.
    * @note Currently implemented as the number obtained by concatenating the binary representation of both arguments.
    */
  def combineSeeds(x: Seed, y: Seed): Seed = {
    Integer.parseInt(Integer.toBinaryString(x) ++ Integer.toBinaryString(y), 2)
  }

  type Signature = ByteString
}
