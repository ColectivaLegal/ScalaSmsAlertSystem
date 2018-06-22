package models

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.{PublishRequest, PublishResult}
import javax.inject.Inject
import play.api.Logger
import play.api.i18n.{Lang, MessagesApi}

import models.Subscriber.SubscriberLangMap

case class Alert(address: String) {

  @Inject() var amazonSns: AmazonSNS = _

  def sendAlert(subscribers: Seq[Subscriber], messagesApi: MessagesApi): Unit = {
    subscribers.foreach { subscriber =>
      implicit val lang: Lang = Lang.get(SubscriberLangMap(subscriber.language.get)).get
      val smsBody: String = messagesApi("action_alert", address)

      val result: PublishResult = amazonSns.publish(
        new PublishRequest()
          .withPhoneNumber(subscriber.phone)
          .withMessage(smsBody)
      )

      Logger.info(s"Sent text message to ${subscriber.phone} with message id ${result.getMessageId}")
    }
  }
}
