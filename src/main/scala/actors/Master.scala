package actors

import actors.Ingestion.Image
import actors.Master.Aggregate
import actors.ParentMaster.{Dark, Data, Light}
import actors.Worker.Luminance
import akka.actor.{Actor, ActorRef, Props}

object Master {
  case object InitializeMaster
  case object WorkerInitialized
  case class FinalResult(luminance: Luminance)
  case class Data(sumOfColor: Int)

  trait LuminanceLabel
  case object Dark extends LuminanceLabel
  case object Light extends LuminanceLabel

  def props(amountOfWorkers: Int, cutOff: Int): Props = Props(
    new Master(amountOfWorkers, cutOff)
  )
}

class Master(amountOfMasters: Int, cutOff: Int) extends Actor {
  val masters: Vector[ActorRef] =
    (0 until amountOfMasters).toVector.map(id =>
      context.actorOf(Props[Master], s"$id-master")
    )

  override def receive: Receive =
    waitingForImage(
      0,
      0,
      masters,
      Set.empty[Int],
      Map.empty[String, Luminance]
    )

  def waitingForImage(
      currentMasterId: Int,
      currentTaskId: Int,
      masters: Vector[ActorRef],
      taskIdSet: Set[Int],
      result: Map[String, Luminance]
  ): Receive = {
    case Image(name, img) =>
      val currentMaster = masters(currentMasterId)
      val w = img.getWidth()
      val h = img.getHeight()
      val imgRGB = img.getRGB(0, 0, w, h, null, 0, w)
      currentMaster ! Data(imgRGB.sum)
      context.become(
        waitingForImage(
          currentMasterId + 1,
          currentTaskId + 1,
          masters,
          taskIdSet + currentTaskId,
          result
        )
      )
    case Aggregate(id, name, luminance) =>
      val newTaskIdSet = taskIdSet - id
      val newResult = result + (name -> Luminance(
        luminance.score + result.getOrElse(name, Luminance(0)).score
      ))
      val processedResult = newResult.map(result => {
        if (result._2.score >= cutOff) (result._1, Light)
        else (result._1, Dark)
      })
      if (newTaskIdSet.isEmpty) context.parent ! processedResult
      else
        context.become(
          waitingForImage(
            currentMasterId,
            currentTaskId,
            masters,
            newTaskIdSet,
            newResult
          )
        )
  }
}
