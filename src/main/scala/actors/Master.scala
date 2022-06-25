package actors

import actors.Master.{Aggregate, Initialize}
import actors.Worker.{Execute, Luminance, Result}
import akka.actor.{Actor, ActorRef, Props}

object Master {
  case object Initialize
  case object WorkerInitialized
  case class Aggregate(sumOfLuminance: Luminance)

  def props(amountOfWorker: Int): Props = Props(new Master(amountOfWorker))
}

class Master(amountOfWorkers: Int) extends Actor {
  override def receive: Receive = waitingForInitialization

  def waitingForInitialization: Receive = { case Initialize =>
    val workers =
      (0 until amountOfWorkers).toVector.map(id =>
        context.actorOf(Props[Worker], s"$id-worker")
      )
    context.become(waitingForData(0, 0, workers, Set.empty[Int], Luminance(0)))
  }

  def waitingForData(
      currentWorkerId: Int,
      currentTaskId: Int,
      workers: Vector[ActorRef],
      taskIdSet: Set[Int],
      result: Luminance
  ): Receive = {
    case Data(arr) =>
      val currentWorker = workers(currentWorkerId)
      currentWorker ! Execute(currentTaskId, arr)
      context.become(
        waitingForData(
          currentWorkerId + 1,
          currentTaskId + 1,
          workers,
          taskIdSet + currentTaskId,
          result
        )
      )
    case Result(id, luminance) =>
      val newTaskIdSet = taskIdSet - id
      val newResult = Luminance(result.score + luminance.score)
      if (newTaskIdSet.isEmpty) context.parent ! Aggregate(newResult)
      else
        context.become(
          waitingForData(
            currentWorkerId,
            currentTaskId,
            workers,
            newTaskIdSet,
            newResult
          )
        )
  }
}
