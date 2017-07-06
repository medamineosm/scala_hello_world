package ScalaCrawler.Actors

import java.net.URI

import ScalaCrawler.Messages._
import ScalaCrawler._
import akka.actor.{Actor, ActorSystem, Props, _}

import scala.language.postfixOps

/**
  * @author Foat Akhmadeev
  *         17/01/16
  */
class Supervisor(system: ActorSystem) extends Actor {
  val indexer = context actorOf Props(new Indexer(self))

  val maxPages = 2
  val maxRetries = 2

  var numVisited = 0
  var toScrap = Set.empty[URI]
  var scrapCounts = Map.empty[URI, Int]
  var host2Actor = Map.empty[String, ActorRef]

  def receive: Receive = {
    case Start(url) =>
      println(s"[Supervisor] starting $url")
      scrap(url)
    case ScrapFinished(url) =>
      println(s"[Supervisor] scraping finished $url")
    case IndexFinished(url, urls) =>
      if (numVisited < maxPages)
        urls.toSet.filter(l => !scrapCounts.contains(l)).foreach(scrap)
      checkAndShutdown(url)
    case ScrapFailure(url, reason) =>
      val retries: Int = scrapCounts(url)
      println(s"[Supervisor] scraping failed $url, $retries, reason = $reason")
      if (retries < maxRetries) {
        countVisits(url)
        host2Actor(url.getHost) ! Scrap(url)
      } else
        checkAndShutdown(url)
  }

  def checkAndShutdown(url: URI): Unit = {
    toScrap -= url
    // if nothing to visit
    println(s"[Supervisor] [${url.getHost}] Number visited page [${scrapCounts.keySet.size}] Vs to visit [${toScrap.size}]")
    if (toScrap.isEmpty) {
      self ! PoisonPill
      system.terminate()
    }
  }

  def scrap(url: URI) = {
    val host = url.getHost
    //println(s"[Supervisor] host = $host")
    if (!host.isEmpty) {
      val actor = host2Actor.getOrElse(host, {
        val buff = system.actorOf(Props(new SiteCrawler(self, indexer)))
        host2Actor += (host -> buff)
        buff
      })

      numVisited += 1
      toScrap += url
      countVisits(url)
      actor ! Scrap(url)
    }
  }

  def countVisits(url: URI): Unit = scrapCounts += (url -> (scrapCounts.getOrElse(url, 0) + 1))
}