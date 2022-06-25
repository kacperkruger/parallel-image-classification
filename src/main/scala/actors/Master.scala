package actors

import actors.Worker.Luminance
import akka.actor.Props

object Master {
  case object Initialize
  case object WorkerInitialized
  case class Aggregate(sumOfLuminance: Luminance)

  def props(amountOfWorker: Int): Props = Props(new Master(amountOfWorker))
}
