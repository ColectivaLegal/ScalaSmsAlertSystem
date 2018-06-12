package models

import models.SubscriberTransitions.{AlertAction, Complete, SelectingLanguage, Unsubscribed}

class SubscriberTransitions(subscriber: Subscriber) {

  def action(input: String): Option[AlertAction] = {
    val trimmedInput = input.trim
    val currentState = SubscriberTransitions.withState(subscriber.state)
    currentState match {
      case Unsubscribed => None
      case SelectingLanguage => None
      case Complete =>
        if (trimmedInput.length > 6 && trimmedInput.substring(0, 6).equalsIgnoreCase("report")) {
          Some(AlertAction(trimmedInput.substring(6).trim))
        }
        else {
          None
        }
    }
  }

  def transition(input: String): (String, Option[Subscriber]) = {
    val trimmedInput = input.trim

    SubscriberTransitions.withState(subscriber.state) match {
      case Unsubscribed => unsubscribedStateAction(trimmedInput)
      case SelectingLanguage => selectingLangAction(trimmedInput)
      case Complete => completeAction(trimmedInput)
    }
  }

  private def unsubscribedStateAction(msg: String): (String, Option[Subscriber]) = {
    if (SubscriberActions.isJoinAction(msg)) {
      ("language_selection_msg", Some(subscriber.copy(state = SelectingLanguage.stateName)))
    }
    else {
      ("subscribe_help_msg", None)
    }
  }

  private def selectingLangAction(msg: String): (String, Option[Subscriber]) = {
    SubscriberTransitions.LanguageSelections
      .get(msg)
      .map(lang => (
        "confirmation_msg",
        Some(subscriber.copy(language = Some(lang), state = Complete.stateName)).asInstanceOf[Option[Subscriber]]
      ))
      .getOrElse(("unsupported_lang_msg", None))
  }

  private def completeAction(msg: String): (String, Option[Subscriber]) = {
    if (SubscriberActions.isChangeLanguageAction(msg)) {
      ("language_selection_msg", Some(subscriber.copy(state = SelectingLanguage.stateName)))
    }
    else if (SubscriberActions.isLeaveAction(msg)) {
      ("unsubscribed_msg", Some(subscriber.copy(state = Unsubscribed.stateName)))
    }
    else if (SubscriberActions.isJoinAction(msg)) {
      ("already_subscribed_msg", None)
    }
    else if (msg.length > 6 && msg.substring(0, 6).equalsIgnoreCase("report")) {
      ("report_msg", None)
    }
    else {
      ("error_msg", None)
    }
  }
}

object SubscriberTransitions {
  sealed trait SubscriptionState { def stateName: String }
  case object Unsubscribed extends SubscriptionState { val stateName = "unsubscribed"}
  case object SelectingLanguage extends SubscriptionState { val stateName="selecting_language"}
  case object Complete extends SubscriptionState { val stateName="complete"}

  case class AlertAction(addr: String)

  val SubscriptionStates: Array[SubscriptionState] = Array(Unsubscribed, SelectingLanguage, Complete)

  def withState(value : String): SubscriptionState = value match {
      case Unsubscribed.stateName => Unsubscribed
      case SelectingLanguage.stateName => SelectingLanguage
      case Complete.stateName => Complete
      case _ => Unsubscribed
  }

  var LanguageSelections = Map(
    "1" -> "eng",
    "2" -> "spa",
    "3" -> "kor",
    "4" -> "cmn",
    "5" -> "vie"
  )
}
