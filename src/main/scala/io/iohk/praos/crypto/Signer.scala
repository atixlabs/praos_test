package io.iohk.praos.crypto


trait Signer {
  def signedWith[T](data: T, privateKey: Key): Signed[T]
  def stripSignature[T](signedData: Signed[T]): (Key, T)
}
