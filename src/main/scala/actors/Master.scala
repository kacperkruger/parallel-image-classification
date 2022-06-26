package actors

import actors.Ingestion.Image
import actors.Master.{Aggregate, Dark, Bright, LuminanceLabel}
import actors.Worker.{Execute, Luminance, Result}
import akka.actor.{Actor, ActorRef, Props}

object Master {
  case object InitializeMaster
  case object WorkerInitialized
  case class FinalResult(luminance: Luminance)
  case class Data(name: String, sumOfColor: Int)
  case class Aggregate(result: List[(String, LuminanceLabel, Int)])

  trait LuminanceLabel {
    def value: String
  }
  case object Dark extends LuminanceLabel {
    override def value: String = "dark"
  }
  case object Bright extends LuminanceLabel {
    override def value: String = "bright"
  }

  def props(amountOfWorkers: Int, cutOff: Int, numberOfTasks: Int): Props =
    Props(
      new Master(amountOfWorkers, cutOff, numberOfTasks)
    )
}

class Master(amountOfWorkers: Int, cutOff: Int, numberOfTasks: Int)
    extends Actor {
  val workers: Vector[ActorRef] =
    (0 until amountOfWorkers).toVector.map(id =>
      context.actorOf(Props[Worker], s"$id-worker")
    )

  override def receive: Receive =
    waitingForImage(
      0,
      workers,
      numberOfTasks,
      List.empty[(String, Luminance)]
    )

  def waitingForImage(
      currentWorkerId: Int,
      workers: Vector[ActorRef],
      remainingTasks: Int,
      result: List[(String, Luminance)]
  ): Receive = {
    case Image(name, img) =>
      val currentWorker = workers(currentWorkerId)
      currentWorker ! Execute(
        name,
        img
      )
      context.become(
        waitingForImage(
          currentWorkerId + 1 % amountOfWorkers,
          workers,
          remainingTasks,
          result
        )
      )
    case Result(name, luminance) =>
      val newRemainingTasks = remainingTasks - 1
      val newResult = result :+ (name, luminance)
      if (newRemainingTasks == 0) {
        val processedResult = newResult.foldLeft(
          List.empty[(String, LuminanceLabel, Int)]
        )((acc, res) => {
          if (res._2.score >= cutOff)
            acc :+ (res._1, Bright, res._2.score)
          else acc :+ (res._1, Dark, res._2.score)
        })
        context.parent ! Aggregate(processedResult)
      } else
        context.become(
          waitingForImage(
            currentWorkerId,
            workers,
            newRemainingTasks,
            newResult
          )
        )
  }
}
