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

    val pk: PublicKey = PublicKey(1234)
    println(pk)

    val sk: Key = PrivateKey(4567)
    println(sk)

    val sd1 = StakeDistribution(pk -> s)
    println(sd1)
    val sd2 = Map[Int, Int](1 -> 2)
  }
}
