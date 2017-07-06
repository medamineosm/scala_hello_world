package ScalaCrawler.Actors

import java.net.URI

import ScalaCrawler.Messages.{Content, Index, IndexFinished}
import akka.actor.{Actor, ActorRef}

/**
  * @author Foat Akhmadeev
  *         17/01/16
  */
class Indexer(supervisor: ActorRef) extends Actor {
  var store = Map.empty[URI, Content]

  def receive: Receive = {
    case Index(url, content) =>
      println(s"[Indexer] [${url.getHost}] saving page $url with ${content.statusCode}")
      store += (url -> content)
      supervisor ! IndexFinished(url, content.urls)
      println(s"[Indexer] [${url.getHost}]store size : ${store.size}")
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    store.foreach(println)
    println(s"[Indexer] store size : ${store.size}")
  }
}