package io.iohk.praos.crypto

import akka.util.ByteString


/**
  * A pre-defined hasher.
  *
  * @param algorithm  A pre-defined algorithm (e.g. "MD5").
  * @note  Precondition: The given algorithm is supported.
  */
case class PredefinedHasher(algorithm: String) extends Hasher {
  override def hash(message: ByteString): Digest = {
    ByteString(java.security.MessageDigest.getInstance(algorithm).digest(message.toArray))
  }
}
