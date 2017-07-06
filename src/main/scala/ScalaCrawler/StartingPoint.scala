package ScalaCrawler

import java.net.URI

import ScalaCrawler.Actors.HighSupervisor
import ScalaCrawler.Messages.StartAll
import akka.actor.{ActorSystem, PoisonPill, Props}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Foat Akhmadeev
  *         17/01/16
  */
object StartingPoint extends App {
  val system = ActorSystem()
  val highSupervisor = system.actorOf(Props(new HighSupervisor(system)))

  var urls = Set.empty[URI]

  urls += new URI("https://www.playtex.fr")
  //urls += new URI("https://www.jardiland.fr")
  //urls += new URI("https://www.foat.me")
  urls += new URI("https://www.1-2-3.fr")
  //urls += new URI("https://www.1-2-3.com")

  val s = urls.size
  println(s"size set or urls = $s")
  highSupervisor ! StartAll(urls)

  Await.result(system.whenTerminated, 180 minutes)

  highSupervisor ! PoisonPill
  system.terminate
}
