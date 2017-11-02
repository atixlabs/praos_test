import org.scalatest.{FlatSpec, Matchers}
import io.iohk.praos.crypto
import io.iohk.praos.crypto.{VerifiableRandomFunction, VerifiableRandomFunctionStubImpl, generateNewRandomValue}
import io.iohk.praos.domain._

class ElectionManagerSpec extends FlatSpec with Matchers {

  trait TestSetup {
    val vrf: VerifiableRandomFunction = VerifiableRandomFunctionStubImpl
  }

  "Given a stakeholder with all stake, ElectionManager" should "allways choose him" in new TestSetup {
    val electionManager = ElectionManager(activeSlotCoefficient = 1, vrf)

    val (publicKey1, privateKey1) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder1 = StakeHolder(privateKey1, publicKey1)
    val stakeDistribution = StakeDistribution(stakeholder1.publicKey -> 10)
    val genesisNonce = generateNewRandomValue()
    val genesis = GenesisBlock(stakeDistribution, genesisNonce)
    val slotInEpoch = SlotInEpoch(epochNumber = 1, slotNumber = 1, firstInEpoch = true)

    val vrfProof = electionManager.isStakeHolderLeader(stakeholder1, genesis, slotInEpoch)
    electionManager.isStakeHolderLeader(stakeholder1, genesis, slotInEpoch).isDefined shouldBe true
  }

  /**
    * This scenario reflects the probability to be chosen without stake. And is not a real scenario,
    * because a stakeholder without stake should never be part of the stakeholder's group
    * that are used to run the consensus, but we added this test in order to test the functionality
    * in terms of probability.
    */
  "Given a stakeholder with zero stake, ElectionManager" should "never choose him" in new TestSetup {
    val electionManager = ElectionManager(activeSlotCoefficient = 0.7, vrf)

    val (publicKey1, privateKey1) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder1 = StakeHolder(privateKey1, publicKey1)
    val (publicKey2, privateKey2) = crypto.generateKeyPair(generateNewRandomValue())
    val stakeholder2 = StakeHolder(privateKey2, publicKey2)
    val stakeDistribution = StakeDistribution(stakeholder1.publicKey -> 0, stakeholder2.publicKey -> 5)
    val genesisNonce = generateNewRandomValue()
    val genesis = GenesisBlock(stakeDistribution, genesisNonce)
    val slotInEpoch = SlotInEpoch(epochNumber = 1, slotNumber = 1, firstInEpoch = true)

    val vrfProof = electionManager.isStakeHolderLeader(stakeholder1, genesis, slotInEpoch)
    electionManager.isStakeHolderLeader(stakeholder1, genesis, slotInEpoch).isDefined shouldBe false
  }
}
