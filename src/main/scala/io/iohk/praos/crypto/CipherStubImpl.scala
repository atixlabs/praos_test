package io.iohk.praos.crypto

import akka.util.ByteString
import io.iohk.praos.util.ByteUtils


object CipherStubImpl extends Cipher {
  def encryptWith(data: ByteString, publicKey: Key): ByteString = ByteUtils.xor(data, publicKey)

  def decryptWith(encryptedData: ByteString, privateKey: Key): Option[ByteString] =
    Option(ByteUtils.xor(encryptedData, getPublicKeyFromPrivateKey(privateKey)))
}
