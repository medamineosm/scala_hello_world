package ScalaCrawler.Actors

import java.net.URI

import ScalaCrawler.Messages.{Start, StartAll}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * Created by OUASMINE Mohammed Amine on 06/07/2017.
  */
class HighSupervisor(system: ActorSystem) extends Actor{

  var client2Actor = Map.empty[String, ActorRef]

  def display(url:URI): Unit ={
    println(s"[Supervisor] starting internal [${url.getHost}] scan for  $url")
    println(s"[Supervisor] creating new actor if doesn't exist yet with Ref [${client2Actor.get(url.getHost)}]")
  }

  def createActorsForHosts(urls: Set[URI]) = {
    urls.foreach(url => createActorForHost(url))
  }

  def createActorForHost(url: URI) = {
    val host = url.getHost

    if(!host.isEmpty){
      val actor = client2Actor.getOrElse(host, {
      val buff = ActorSystem().actorOf(Props(new Supervisor(system)))
        client2Actor += (host -> buff)
        buff
      })
      display(url)
      actor ! Start(url)
    }else{
      println(s"Error : Empty host for $url")
    }

  }

  override def receive: Receive = {
    case StartAll(urls) => {
      createActorsForHosts(urls)
    }
  }
}
