package actors

import actors.Ingestion.StartIngestion
import actors.Supervisor.{Start, Stop}
import akka.actor.{Actor, Props}

object Supervisor {
  case object Start
  case object Stop

  def props(
      inDir: String,
      outDir: String,
      cutOff: Int,
      numberOfWorker: Int
  ): Props = Props(new Supervisor(inDir, outDir, cutOff, numberOfWorker))
}

class Supervisor(
    inDir: String,
    outDir: String,
    cutOff: Int,
    numberOfWorker: Int
) extends Actor {
  override def receive: Receive = {
    case Start =>
      val ingestion = context.actorOf(
        Ingestion.props(inDir, outDir, cutOff, numberOfWorker)
      )
      ingestion ! StartIngestion
    case Stop =>
      context.system.terminate()
  }
}
