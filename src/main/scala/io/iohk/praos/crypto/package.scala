package io.iohk.praos

import akka.util.ByteString
import scala.util.Random
import scala.math.abs


package object crypto {

  type Key = ByteString
  type KeyPair = (Key, Key)
  type RandomValue = Int

  // Dummy implementation to simulate a public-private key schema.
  val keyLength = 32

  private def genKey = (s: RandomValue, length: Int) => ByteString(Array.fill(length)(s.toByte))

  def generateNewRandomValue(): RandomValue = abs(Random.nextInt())

  /**
    * @return (publicKey, privateKey)
    */
  def generateKeyPair(seed: RandomValue): KeyPair = {
    (genKey(seed + 1, keyLength), genKey(seed, keyLength))
  }

  def getPublicKeyFromPrivateKey(privateKey: Key): Key = {
    privateKey map (byte => (byte + 1).toByte)
  }

  /**
    * The seed for a PRNG.
    */
  type Seed = Int

  /**
    * Operation used to "combine" seeds. According to the Praos Formalization, it should be associative.
    */
  def combineSeeds(x: Seed, y: Seed): Seed = abs(x + y)

  // TODO: Change to a realistic value.
  val SeedNonce: Seed = 123

  type Signature = ByteString
}
