package io.iohk.praos

import java.security.SecureRandom
import akka.util.ByteString
import fr.cryptohash.{Keccak256, Keccak512}
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.asn1.x9.X9ECParameters
import org.spongycastle.crypto.AsymmetricCipherKeyPair
import org.spongycastle.crypto.generators.ECKeyPairGenerator
import org.spongycastle.crypto.params._

package object crypto {

  // Original files
  type Key = ByteString
  type RandomValue = ByteString
  type Signature = ByteString
  type Seed = ByteString

  val KEY_LENGTH = 32
  val RANDOM_LENGTH = 32

  val curveParams: X9ECParameters = SECNamedCurves.getByName("secp256k1")
  val curve: ECDomainParameters = new ECDomainParameters(curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)

  def kec256(input: Array[Byte], start: Int, length: Int): Array[Byte] = {
    val digest = new Keccak256
    digest.update(input, start, length)
    digest.digest
  }

  def kec256(input: Array[Byte]*): Array[Byte] = {
    val digest: Keccak256 = new Keccak256
    input.foreach(i => digest.update(i))
    digest.digest
  }

  def kec256(input: ByteString): ByteString =
    ByteString(kec256(input.toArray))

  def kec512(input: Array[Byte]*): Array[Byte] = {
    val digest = new Keccak512
    input.foreach(i => digest.update(i))
    digest.digest
  }

  def generateKeyPair(secureRandom: SecureRandom): AsymmetricCipherKeyPair = {
    val generator = new ECKeyPairGenerator
    generator.init(new ECKeyGenerationParameters(curve, secureRandom))
    generator.generateKeyPair()
  }

  def secureRandomByteString(secureRandom: SecureRandom, length: Int): ByteString =
    ByteString(secureRandomByteArray(secureRandom, length))

  def secureRandomByteArray(secureRandom: SecureRandom, length: Int): Array[Byte] = {
    val bytes = Array.ofDim[Byte](length)
    secureRandom.nextBytes(bytes)
    bytes
  }

  /** @return (privateKey, publicKey) pair */
  def keyPairToByteArrays(keyPair: AsymmetricCipherKeyPair): (Array[Byte], Array[Byte]) = {
    val prvKey = keyPair.getPrivate.asInstanceOf[ECPrivateKeyParameters].getD.toByteArray
    val pubKey = keyPair.getPublic.asInstanceOf[ECPublicKeyParameters].getQ.getEncoded(false).tail
    (prvKey, pubKey)
  }

  def keyPairToByteStrings(keyPair: AsymmetricCipherKeyPair): (ByteString, ByteString) = {
    val (prv, pub) = keyPairToByteArrays(keyPair)
    (ByteString(prv), ByteString(pub))
  }

  def keyPairFromPrvKey(prvKeyBytes: Array[Byte]): AsymmetricCipherKeyPair = {
    val privateKey = BigInt(1, prvKeyBytes)
    keyPairFromPrvKey(privateKey)
  }

  def keyPairFromPrvKey(prvKey: BigInt): AsymmetricCipherKeyPair = {
    val publicKey = curve.getG.multiply(prvKey.bigInteger).normalize()
    new AsymmetricCipherKeyPair(new ECPublicKeyParameters(publicKey, curve), new ECPrivateKeyParameters(prvKey.bigInteger, curve))
  }

  def pubKeyFromPrvKey(prvKey: Array[Byte]): Array[Byte] =
    keyPairToByteArrays(keyPairFromPrvKey(prvKey))._2


  def pubKeyFromPrvKey(prvKey: ByteString): ByteString =
    ByteString(pubKeyFromPrvKey(prvKey.toArray))

  def generateNewRandomValue(): RandomValue = secureRandomByteString(new SecureRandom(), RANDOM_LENGTH)

  /**
    * Operation used to "combine" seeds. According to the Praos Formalization, it should be associative.
    */
  def combineSeeds(x: Seed, y: Seed): Seed = x ++ y
}
