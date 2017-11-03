package io.iohk.praos.crypto

import akka.util.ByteString


object CipherStubImpl extends Cipher {
  def encryptWith(data: ByteString, publicKey: Key): ByteString = XOR(data, publicKey)

  def decryptWith(encryptedData: ByteString, privateKey: Key): Option[ByteString] =
    Option(XOR(encryptedData, getPublicKeyFromPrivateKey(privateKey)))
}
