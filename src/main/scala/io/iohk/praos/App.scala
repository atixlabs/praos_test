package io.iohk.praos

import io.iohk.praos.domain.{RelativeStake, Stake}
import io.iohk.praos.crypto.{Key, PrivateKey, PublicKey}
import io.iohk.praos.domain.MyTypes._


object Foo {
  def apply (x: Int, y: Int): Foo = Foo(x + y)
}

case class Foo private (n: Int)

object App {

  def main(args: Array[String]): Unit = {

    val s = Stake(123)
    println(s)

    val rs = RelativeStake(1.2)
    println(rs)

    println(Foo(13))

    println(RelativeStake(Stake(5), Stake(10)))

    val pk1: PublicKey = PublicKey(1234)
    println(pk1)

    val pk2: PublicKey = PublicKey(8901)
    println(pk2)

    val sk: Key = PrivateKey(4567)
    println(sk)

    val sd1 = StakeDistribution(pk1 -> s, pk2 -> s)
    println(sd1)

    val rs1 = RelativeStake(pk1, sd1)
    println(rs1)
  }
}
