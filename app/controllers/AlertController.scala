package controllers

import javax.inject.{Inject, Singleton}
import models.SubscriberRepository
import play.api.cache.CacheApi
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

import AlertController._

@Singleton
class AlertController @Inject()(cc: ControllerComponents, repo: SubscriberRepository, messagesApi: MessagesApi, cache: CacheApi)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport with AuthenticatedActionSupport {

  def get = authenticatedAction { implicit request =>
    Ok(views.html.alert(AlertForm, this))
  }

  def post = authenticatedActionAsync { implicit request =>
    AlertForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.alert(formWithErrors, this)))
      },
      alert => {
        repo.listActive().map { subscribers =>
          alert.sendAlert(subscribers, messagesApi)
          Redirect(routes.HomeController.index()).flashing("success" -> "Done! Messages sent")
        }
      }
    )
  }
}

private object AlertController {

  val AlertForm = Form(
    mapping(
      "address" -> nonEmptyText
    )(models.Alert.apply)(models.Alert.unapply)
  )
}
