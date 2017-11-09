package io.iohk.praos.crypto

import javax.xml.bind.DatatypeConverter
import akka.util.ByteString
import org.scalatest.{FlatSpec, Matchers}


/**
  * Unit tests for class [[io.iohk.praos.crypto.PredefinedHasher]].
  */
class PredefinedHasherSpec extends FlatSpec with Matchers {

  "The message \"AtixLabs\"" should "be hashed to digest \"b98ae57c8040238c8d2edca55493c40b\" by the MD5 algorithm" in {
    val md5Hasher = PredefinedHasher("MD5")
    val message = ByteString("AtixLabs")
    val digest = ByteString(DatatypeConverter.parseHexBinary("b98ae57c8040238c8d2edca55493c40b"))

    md5Hasher.hash(message) shouldBe digest
  }
}