package io.iohk.praos.crypto

import akka.util.ByteString

/**
  * TODO: Implement with ECDSA
  */
object SignatureProviderImpl extends SignatureProvider {
  override def getSignature(data: ByteString, privateKey: Key): Signature =
    pubKeyFromPrvKey(privateKey)

  override def getPublicKeyFromSignature(signature: Signature): Key = signature
}
