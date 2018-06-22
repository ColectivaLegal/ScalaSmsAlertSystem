package models

import play.api.libs.json._

case class Subscriber(id: Long, phone: String, language: Option[String], state: String)

object Subscriber {
  implicit val subscriberFormat: OFormat[Subscriber] = Json.format[Subscriber]

  // TODO: remove this and use the 3 letter
  val SubscriberLangMap = Map(
    "eng" -> "en",
    "spa" -> "es",
    "kor" -> "ko",
    "vie" -> "vi",
    "cmn" -> "zh"
  )
}
