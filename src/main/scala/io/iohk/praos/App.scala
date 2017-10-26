package io.iohk.praos

import io.iohk.praos.domain.RelativeStake
import io.iohk.praos.crypto.Key
import io.iohk.praos.domain._


object Foo {
  def apply (x: Int, y: Int): Foo = Foo(x + y)
}

case class Foo private (n: Int)

object App {

  def main(args: Array[String]): Unit = {

    val s:Stake = 123
    println(s)

    val rs = RelativeStake(1.2)
    println(rs)

    println(Foo(13))

    println(RelativeStake(5, 10))

    var pk1: Key = akka.util.ByteString("123")
    println(pk1)

    val pk2: Key = akka.util.ByteString("456")
    println(pk2)

    val sk: Key = akka.util.ByteString("789")
    println(sk)

    val sd1 = StakeDistribution(pk1 -> s, pk2 -> s)
    println(sd1)

    val rs1 = RelativeStake(pk1, sd1)
    println(rs1)
  }
}
