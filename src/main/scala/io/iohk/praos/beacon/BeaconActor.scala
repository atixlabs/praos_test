package io.iohk.praos.beacon

import akka.actor._
import io.iohk.praos.beacon.BeaconActor.BroadcastEndSlot
import scala.math.max
import scala.concurrent.duration._

class BeaconActor(
    subscribers: Seq[ActorRef],
    initialTimeInMilliseconds: Long,
    epochLength: Long,
    slotDurationInMilliseconds: Long = 3000,
    externalSchedulerOpt: Option[Scheduler] = None)
  extends Actor {

  private val scheduler = externalSchedulerOpt getOrElse context.system.scheduler

  /**
    * TODO: Add comment about initial delay.
    */
  private val syncBeacon = scheduler.schedule(
    initialDelay = max(0, initialTimeInMilliseconds - System.currentTimeMillis()).millisecond,
    interval = slotDurationInMilliseconds.millisecond,
    receiver = self,
    message = BroadcastEndSlot
  )

  private var currentSlot = SlotInEpoch(
    epochNumber = 0,
    slotNumber = 0,
    firstInEpoch = false,
  )

  override def receive: Receive = {
    case BroadcastEndSlot => {
      val nextSlot = SlotInEpochCalculator.calculate(System.currentTimeMillis())
      subscribers.foreach( subscriber => {
        subscriber ! BroadcastEndSlot(nextSlot)
      })
    }
  }

  private val slotInEpochCalculator = SlotInEpochCalculator(
    initialTimeInMilliseconds,
    epochLength,
    slotDurationInMilliseconds
  )

}

object BeaconActor {

  case class BroadcastEndSlot(nextSlot: SlotInEpoch)

}
