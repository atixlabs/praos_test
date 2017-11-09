package io.iohk.praos.crypto

import akka.util.ByteString


trait Cipher {
  def encryptWith(data: ByteString, publicKey: Key): ByteString
  def decryptWith(encryptedData: ByteString, privateKey: Key): Option[ByteString]
}
