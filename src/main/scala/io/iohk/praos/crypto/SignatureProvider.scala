package io.iohk.praos.crypto

import akka.util.ByteString


trait SignatureProvider {
  /**
    * @return signature
    */
  def getSignature(data: ByteString, privateKey: Key): Signature

  /**
    * @return (publicKey, data)
    */
  def splitSignature(signature: Signature): (Key, ByteString)
}
