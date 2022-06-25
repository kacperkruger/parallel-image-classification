package actors

import actors.Worker.{Execute, Luminance, Result}
import akka.actor.Actor

import java.awt.Color

object Worker {
  case class Execute(id: Int, task: Array[Int])
  case class Result(id: Int, luminance: Luminance)
  case class Luminance(score: Double)
}

trait WorkerHandler {
  def calculateLuminance(arr: Array[Int]): Luminance = {
    val sumOfRGB = arr.foldLeft((0, 0, 0))((acc, pixel) => {
      val c = new Color(pixel)
      (acc._1 + c.getRed, acc._2 + c.getGreen, acc._3 + c.getBlue)
    })
    Luminance(
      0.2126 * sumOfRGB._1 + 0.75152 * sumOfRGB._2 + 0.0722 * sumOfRGB._3
    )
  }
}

class Worker extends Actor with WorkerHandler {
  override def receive: Receive = { case Execute(id, task) =>
    sender() ! Result(id, calculateLuminance(task))
  }
}
