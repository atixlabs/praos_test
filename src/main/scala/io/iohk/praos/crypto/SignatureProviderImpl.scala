package io.iohk.praos.crypto

import akka.util.ByteString


object SignatureProviderImpl extends SignatureProvider {
  def getSignature(data: ByteString, privateKey: Key): Signature =
    getPublicKeyFromPrivateKey(privateKey) ++ data

  def splitSignature(signature: Signature): (Key, ByteString) = signature.splitAt(keyLength)
}
