package ScalaCrawler.Messages

import java.net.URI

/**
  * @author Foat Akhmadeev
  *         17/01/16
  */
case class StartAll(urls: Set[URI])
case class Start(url: URI)
case class Scrap(url: URI)
case class Index(url: URI, content: Content)
case class Content(title: String, meta: String, statusCode:Int, statusMsg:String, urls: List[URI], html: String)
case class ScrapFinished(url: URI)
case class IndexFinished(url: URI, urls: List[URI])
case class ScrapFailure(url: URI, reason: Throwable)
