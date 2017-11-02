package io.iohk.praos.crypto


case class Signed[T](value: T, signature: Signature)
