package io.iohk.praos.crypto


case class Signed[T <: Serializable](value: T, signature: Signature) extends Serializable {
  override def toString = value.toString
}
