package controllers

import javax.inject.{Inject, Singleton}

import com.twilio.`type`.PhoneNumber
import com.twilio.rest.api.v2010.account.Message
import models.SubscriberRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FollowupController @Inject()(cc: ControllerComponents, repo: SubscriberRepository, messagesApi: MessagesApi)
                                  (implicit ec: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val followUpForm = Form(
    mapping(
      "numberPeople" -> number,
      "city" -> nonEmptyText,
      "targetName" -> nonEmptyText,
      "targetPhoneNumber" -> nonEmptyText
    )(models.FollowUp.apply)(models.FollowUp.unapply)
  )
  def get = Action { implicit request =>
    Ok(views.html.followup(followUpForm))
  }

  def post = Action.async { implicit request =>
    followUpForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.followup(formWithErrors)))
      },
      followup => {
        repo.listActive().map { subscribers =>
          val messages = subscribers.map { subscriber =>
            implicit val lang: Lang = Lang.get("en").get
            Message
              .creator(new PhoneNumber(subscriber.phone), // to
                new PhoneNumber(sys.env("TWILIO_PHONE")), // from
                messagesApi("follow_up", followup.numberPeople, followup.city, followup.targetName, followup.targetPhoneNumber))
              .create()
          }
          Redirect(routes.HomeController.index())
            .flashing("success" -> ("Done! Messages sent with IDs " + messages.map(_.getSid()).mkString(",")))
        }
      }
    )
  }
}
