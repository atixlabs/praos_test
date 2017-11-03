//package io.iohk.praos.domain
//
//import io.iohk.praos.crypto._
//import org.scalatest.{FlatSpec, Matchers}
//
//
///**
//  * Unit tests for class [[io.iohk.praos.domain.Block]].
//  */
//class BlockSpec extends FlatSpec with Matchers {
//
//  var publicKey1: Key = akka.util.ByteString("1")
//  val publicKey2: Key = akka.util.ByteString("2")
//  val publicKey3: Key = akka.util.ByteString("3")
//
//  val stakeDistribution = StakeDistributionImpl(Map(publicKey1 -> 10, publicKey2 -> 2, publicKey3 -> 8))
//  val nonce: Seed = 2
//
//  val genesisBlock = GenesisBlock(stakeDistribution, nonce)
//
//  val transaction1 = Transaction(publicKey1, publicKey2, 3)
//  val transaction2 = Transaction(publicKey2, publicKey1, 1)
//
//  val prevBlockHash: Hasher#Digest = akka.util.ByteString("abc")
//  val state = Some(prevBlockHash)
//  val slotNumber: SlotNumber = 123
//  val data = List(transaction1, transaction2)
//  val proof = VrfProof(456, akka.util.ByteString("def"))
//  val seed: Seed = 1
//  val blockNonce = (seed, VrfProof(789, akka.util.ByteString("ghi")))
//  val signature: Signature = akka.util.ByteString("jkl")
//  val block = Block(state, slotNumber, data, proof, blockNonce, signature)
//
//  "The application of a block to an initial genesis block" should
//    "apply all transactions in the block and calculate the new nonce" in {
//    val newGenesisBlock = genesisBlock.applyBlock(block)
//    val expectedStakeDistribution = StakeDistribution(publicKey1 -> 8, publicKey2 -> 4, publicKey3 -> 8)
//    newGenesisBlock.genesisDistribution should contain theSameElementsAs expectedStakeDistribution
//    newGenesisBlock.genesisNonce shouldBe 3 // 2 ++ 1
//  }
//}
