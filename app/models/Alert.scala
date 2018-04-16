package models

import com.twilio.rest.api.v2010.account.Message
import com.twilio.`type`.PhoneNumber
import play.api.i18n.{Lang, MessagesApi}

import scala.concurrent.ExecutionContext

case class Alert(address: String) {
  val subscriberLangMap = Map(
    "eng" -> "en",
    "spa" -> "es",
    "kor" -> "ko",
    "vie" -> "vi",
    "cmn" -> "zh"
  )

  def sendAlert(subscribers: Seq[Subscriber], messagesApi: MessagesApi) = {
    subscribers.map { subscriber =>
      implicit val lang: Lang = Lang.get(subscriberLangMap.get(subscriber.language.get).get).get
      Message
        .creator(new PhoneNumber(subscriber.phone), // to
          new PhoneNumber(sys.env("TWILIO_PHONE")), // from
          messagesApi("action_alert", address))
        .create()
    }
  }
}
