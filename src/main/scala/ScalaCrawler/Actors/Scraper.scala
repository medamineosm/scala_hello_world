package ScalaCrawler.Actors

import java.net.URI

import ScalaCrawler.Messages.{Content, Index, Scrap, ScrapFinished}
import akka.actor.{Actor, ActorRef}
import org.apache.commons.validator.routines.UrlValidator
import org.jsoup.Jsoup

import scala.collection.JavaConverters._

/**
  * @author Foat Akhmadeev
  *         17/01/16
  */
class Scraper(indexer: ActorRef) extends Actor {
  val urlValidator = new UrlValidator()

  def receive: Receive = {
    case Scrap(url) =>
      println(s"[Scraper] scraping $url")
      val content = parse(url)
      sender() ! ScrapFinished(url)
      indexer ! Index(url, content)
  }

  def parse(url: URI): Content = {
    val link: String = url.toString
    val response = Jsoup.connect(link).ignoreContentType(true).followRedirects(true)
      .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").execute()

    val contentType: String = response.contentType
    val statusCode:Int = response.statusCode()
    val statusMsg:String = response.statusMessage()

    if (contentType.startsWith("text/html")) {
      val doc = response.parse()
      val html = doc.html()
      val title: String = doc.getElementsByTag("title").asScala.map(e => e.text()).head
      val descriptionTag = doc.getElementsByTag("meta").asScala.filter(e => e.attr("name") == "description")
      val description = if (descriptionTag.isEmpty) "" else descriptionTag.map(e => e.attr("content")).head
      val links: List[URI] = doc.getElementsByTag("a").asScala.map(e => e.attr("href")).filter(s =>
        urlValidator.isValid(s)).map(link => new URI(link)).toList
      Content(title, description, statusCode, statusMsg, links, html)
    } else {
      // e.g. if this is an image
      Content(link, contentType,statusCode, statusMsg, List(), null)
    }
  }
}

