package controllers

import javax.inject.{Inject, Singleton}

import models.SubscriberRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
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
          val messages = alert.sendAlert(subscribers, messagesApi)
          Redirect(routes.HomeController.index())
            .flashing("success" -> ("Done! Messages sent with IDs " + messages.map(_.getSid()).mkString(",")))
        }
      }
    )
  }
}
