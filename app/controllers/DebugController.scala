package controllers

import javax.inject.{Inject, Singleton}

import models.{Subscriber, SubscriberRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DebugController @Inject()(cc: ControllerComponents, repo: SubscriberRepository, messagesApi: MessagesApi)
  (implicit ec: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val addSubscriberForm = Form(
    mapping(
      "id" -> longNumber,
      "phone_number" -> nonEmptyText,
      "language" -> optional(text),
      "state" -> nonEmptyText
    )(models.Subscriber.apply)(models.Subscriber.unapply)
  )

  def get = Action.async { implicit request =>
    repo
      .list()
      .map((subscribers: Seq[Subscriber]) => {
        Ok(views.html.debug(addSubscriberForm, subscribers))
      })
  }

  def post: Action[AnyContent] = Action.async { implicit request =>
    addSubscriberForm.bindFromRequest.fold(
      (formWithErrors: Form[Subscriber]) => {
        Future.successful(BadRequest(views.html.debug(formWithErrors, Seq.empty)))
      },
      (subscriber: Subscriber) => {
        repo
          .getOrCreate(subscriber.phone)
          .map(realSub => {
            Redirect(routes.DebugController.get())
          })
      }
    )
  }
}
