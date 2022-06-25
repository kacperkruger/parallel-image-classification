package actors

import actors.Worker.Luminance
import akka.actor.Props

object ParentMaster {
  case object Initialize
  case object WorkerInitialized
  case class FinalResult(sumOfLuminance: Luminance)

  def props(amountOfMasters: Int): Props = Props(new Master(amountOfMasters))
}
