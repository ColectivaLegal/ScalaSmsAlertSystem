package models

import com.twilio.rest.api.v2010.account.Message
import com.twilio.`type`.PhoneNumber
import play.api.i18n.{Lang, MessagesApi}

import scala.concurrent.ExecutionContext

case class Alert(address: String) {
  def sendAlert(subscribers: Seq[Subscriber], messagesApi: MessagesApi) = {
    subscribers.map { subscriber =>
      implicit val lang: Lang = Lang.get("en").get
      Message
        .creator(new PhoneNumber(subscriber.phone), // to
          new PhoneNumber(sys.env("TWILIO_PHONE")), // from
          messagesApi("action_alert", address))
        .create()
    }
  }
}
