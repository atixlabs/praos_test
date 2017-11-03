package io.iohk.praos.crypto


trait Signer {
  def signedWith[T <: Serializable](data: T, privateKey: Key): Signed[T]
  def stripSignature[T <: Serializable](signedData: Signed[T]): (Key, T)
}
