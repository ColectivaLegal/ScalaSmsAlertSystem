package controllers

import javax.inject.{Inject, Singleton}

import com.twilio.twiml.{Body, Message, MessagingResponse}
import models.{Alert}
import models.{SubscriberAction, AlertAction}
import models.{SubscriberRepository, SubscriberTransitions}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext


@Singleton
class TwilioController @Inject()(cc: ControllerComponents, repo: SubscriberRepository, messagesApi: MessagesApi)
                                (implicit ec: ExecutionContext)  extends AbstractController(cc) {
  case class TwilioData(from: String, body: String)
  val twilioForm = Form(
    mapping(
      "From" -> text,
      "Body" -> text,
    )(TwilioData.apply)(TwilioData.unapply)
  )

  val subscriberLangMap = Map(
    "eng" -> "en",
    "spa" -> "es",
    "kor" -> "ko",
    "vie" -> "vi",
    "cmn" -> "zh",
  )

  def messagePost = Action.async { implicit request =>
    val twilioData = twilioForm.bindFromRequest.get
    repo.getOrCreate(twilioData.from).map { subscriber =>
      val subscriberTransition = new SubscriberTransitions(subscriber)
      val action = subscriberTransition.action(twilioData.body)
      if (action.isDefined) {
        performAction(action.get)
      }
      val (responseMessage, updatedSubscriber) = subscriberTransition.transition(twilioData.body)
      //TODO: Need to fix this to use the updated language of the subscriber.
      val lang : Lang = if (updatedSubscriber.isDefined) {
        repo.update(updatedSubscriber.get)
        Lang.get(subscriberLangMap.get(updatedSubscriber.get.language.get).getOrElse("en")).get
      } else {
        Lang.get(subscriberLangMap.get(subscriber.language.get).getOrElse("en")).get
      }
      val message = new Message.Builder()
        .body(new Body(messagesApi(responseMessage)(lang)))
        .build();
      val response = new MessagingResponse.Builder()
        .message(message)
        .build();
      Ok(response.toXml()).as("application/xml")
    }
  }

  def performAction(action: SubscriberAction) = {
    action match {
      case AlertAction(addr) =>
        val alert = Alert(addr)
        repo.listActive().map { subscribers =>
          alert.sendAlert(subscribers, messagesApi)
        }
    }
  }
}
