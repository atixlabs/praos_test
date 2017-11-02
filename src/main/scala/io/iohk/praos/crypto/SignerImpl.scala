package io.iohk.praos.crypto

import akka.util.ByteString


object SignerImpl extends Signer {
  override def signedWith[T <: Serializable](data: T, privateKey: Key): Signed[T] = {
    // FIXME: Hasher, cipher and signature provider are hard-coded for simplicity.
    val publicKey = getPublicKeyFromPrivateKey(privateKey)
    val hasher = PredefinedHasher("MD5")
    val serializedData = ByteString(data.toString)
    val hashedData = hasher.hash(serializedData)
    val encryptedData = CipherStubImpl.encryptWith(hashedData, publicKey)
    val signature = SignatureProviderImpl.getSignature(encryptedData, privateKey)
    Signed[T](data, signature)
  }

  override def stripSignature[T](signedData: Signed[T]): (Key, T) = {
    val (key, _) = SignatureProviderImpl.splitSignature(signedData.signature)
    (key, signedData.value)
  }
}
