package io.iohk.praos.crypto

import akka.util.ByteString


object SignatureProviderImpl extends SignatureProvider {
  override def getSignature(data: ByteString, privateKey: Key): Signature =
    getPublicKeyFromPrivateKey(privateKey) ++ data

  override def getPublicKeyFromSignature(signature: Signature): Key = signature.splitAt(keyLength)._1
}
