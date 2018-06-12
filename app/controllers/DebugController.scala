package controllers

import javax.inject.{Inject, Singleton}
import models.{Subscriber, SubscriberRepository}
import play.api.cache.CacheApi
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

import DebugController._

@Singleton
class DebugController @Inject()(cc: ControllerComponents, repo: SubscriberRepository, messagesApi: MessagesApi, cache: CacheApi)
  (implicit ec: ExecutionContext) extends AbstractController(cc)
  with play.api.i18n.I18nSupport with AuthenticatedActionSupport {

  def get = authenticatedActionAsync(implicit request =>
    repo
      .list()
      .map((subscribers: Seq[Subscriber]) => {
        Ok(views.html.debug(AddSubscriberForm, subscribers, this))
      })
  )

  def post: Action[AnyContent] = authenticatedActionAsync(implicit request =>
    AddSubscriberForm
      .bindFromRequest
      .fold(
        (formWithErrors: Form[Subscriber]) => {
          Future.successful(BadRequest(views.html.debug(formWithErrors, Seq.empty, this)))
        },
        (subscriber: Subscriber) => {
          repo
            .create(subscriber.phone, subscriber.language, subscriber.state)
            .map(realSub => {
              Redirect(routes.DebugController.get())
            })
        }
      ))
}

private object DebugController {

  val AddSubscriberForm = Form(
    mapping(
      "phone_number" -> nonEmptyText,
      "language" -> optional(text),
      "state" -> nonEmptyText
    )
    ((number, lang, state) => models.Subscriber(0, number, lang, state))
    (subscriber => Some((subscriber.phone, subscriber.language, subscriber.state)))
  )
}
