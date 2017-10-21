package models

import play.api.libs.json._

case class Subscriber(id: Long, phone: String, language: Option[String], state: String)

object Subscriber {
  implicit val subscriberFormat = Json.format[Subscriber]

}
