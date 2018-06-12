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

import FollowupController._

@Singleton
class FollowupController @Inject()(cc: ControllerComponents, repo: SubscriberRepository, messagesApi: MessagesApi)
                                  (implicit ec: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport with AuthenticatedActionSupport {

  def get = authenticatedAction { implicit request =>
    Ok(views.html.followup(FollowUpForm, this))
  }

  def post = authenticatedActionAsync { implicit request =>
    FollowUpForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.followup(formWithErrors, this)))
      },
      followup => {
        repo.listActive().map { subscribers =>
          val messages = subscribers.map { subscriber =>
            implicit val lang: Lang = Lang.get("en").get
            val personWord = if (followup.numberPeople == 1) "person" else "people"
            Message
              .creator(new PhoneNumber(subscriber.phone), // to
                new PhoneNumber(sys.env("TWILIO_PHONE")), // from
                messagesApi("follow_up", followup.numberPeople, personWord, followup.city, followup.targetName, followup.targetPhoneNumber))
              .create()
          }
          Redirect(routes.HomeController.index())
            .flashing("success" -> ("Done! Messages sent with IDs " + messages.map(_.getSid()).mkString(",")))
        }
      }
    )
  }
}

private object FollowupController {

  private val FollowUpForm = Form(
    mapping(
      "numberPeople" -> number,
      "city" -> nonEmptyText,
      "targetName" -> nonEmptyText,
      "targetPhoneNumber" -> nonEmptyText
    )(models.FollowUp.apply)(models.FollowUp.unapply)
  )
}
