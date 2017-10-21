package controllers

import javax.inject.{Inject, Singleton}

import com.twilio.rest.api.v2010.account.Message
import com.twilio.`type`.PhoneNumber
import models.SubscriberRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AlertController @Inject()(cc: ControllerComponents, repo: SubscriberRepository, messagesApi: MessagesApi)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val alertForm = Form(
    mapping(
      "address" -> nonEmptyText
    )(models.Alert.apply)(models.Alert.unapply)
  )
  def get = Action { implicit request =>
    Ok(views.html.alert(alertForm))
  }

  def post = Action.async { implicit request =>
    alertForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.alert(formWithErrors)))
      },
      alert => {
        repo.listActive().map { subscribers =>
          val messages = subscribers.map { subscriber =>
            implicit val lang: Lang = Lang.get("en").get
            Message
              .creator(new PhoneNumber(subscriber.phone), // to
                new PhoneNumber(sys.env("TWILIO_PHONE")), // from
                messagesApi("action_alert", alert.address))
              .create()
          }
          Redirect(routes.HomeController.index())
            .flashing("success" -> ("Done! Messages sent with IDs " + messages.map(_.getSid()).mkString(",")))
        }
      }
    )
  }
}
