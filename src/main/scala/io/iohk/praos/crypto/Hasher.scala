package io.iohk.praos.crypto

import akka.util.ByteString


/**
  * A calculator of hash values.
  */
trait Hasher {

  /** Message digest. */
  type Digest = ByteString

  /**
    * Hashes a message.
    *
    * @param message  The message to be hashed.
    * @return  The message digest.
    */
  def hash(message: ByteString): Digest
}
