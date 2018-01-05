package models

import models.SubscriberTransitions.{Complete, SelectingLanguage, SubscriptionState, Unsubscribed}

sealed trait SubscriberAction
case class AlertAction(addr: String) extends SubscriberAction

class SubscriberTransitions(subscriber : Subscriber) {
  var joinMessages = Array(
    "join",
    "suscribirse",
    "등록",
    "加入",
    "Tham gia"
  )

  var changeLanguageMessages = Array(
    "change language",
    "cambio de lengua",
    "언어변경",
    "改變語言",
    "Thay đổi ngôn ngữ"
  )

  var leaveMessages = Array(
    "leave",
    "abandonar",
    "탈퇴",
    "離開",
    "Rời khỏi"
  )

  var languageSelections = Map(
    "1" -> "eng",
    "2" -> "spa",
    "3" -> "kor",
    "4" -> "cmn",
    "5" -> "vie"
  )

  def action(input: String): Option[SubscriberAction] = {
    val trimmedInput = input.trim
    val currentstate = SubscriberTransitions.withName(subscriber.state);
    currentstate match {
      case Unsubscribed =>
        None
      case SelectingLanguage =>
        None
      case Complete =>
        if (trimmedInput.length > 6 && trimmedInput.substring(0, 6).equalsIgnoreCase("report")) {
          return Some(AlertAction(trimmedInput.substring(6).trim))
        } else {
          return None
        }
    }
  }

  def transition(input : String):(String, Option[Subscriber]) = {
    val trimmedInput = input.trim
    val currentstate = SubscriberTransitions.withName(subscriber.state);
    currentstate match {
      case Unsubscribed =>
        if (joinMessages.exists { x => trimmedInput.equalsIgnoreCase(x) }) {
          val newSubscriber = subscriber.copy(state = SelectingLanguage.name)
          return ("language_selection_msg", Some(newSubscriber))
        } else {
          return ("subscribe_help_msg", None)
        }
      case SelectingLanguage =>
        if (languageSelections.contains(trimmedInput)) {
          val newSubscriber = subscriber.copy(language = languageSelections.get(trimmedInput), state = Complete.name)
          return ("confirmation_msg", Some(newSubscriber))
        } else {
          return ("unsupported_lang_msg", None)
        }
      case Complete =>
        if (changeLanguageMessages.exists { x => trimmedInput.equalsIgnoreCase(x) }) {
          val newSubscriber = subscriber.copy(state = SelectingLanguage.name)
          return ("language_selection_msg", Some(newSubscriber))
        } else if (leaveMessages.exists { x => trimmedInput.equalsIgnoreCase(x) }) {
          val newSubscriber = subscriber.copy(state = Unsubscribed.name)
          return ("unsubscribed_msg", Some(newSubscriber))
        } else if (joinMessages.exists { x => trimmedInput.equalsIgnoreCase(x) }) {
          return ("already_subscribed_msg", None)
        } else if (trimmedInput.length > 6 && trimmedInput.substring(0, 6).equalsIgnoreCase("report")) {
          return ("report_msg", None)
        } else {
          return ("error_msg", None)
        }
    }
  }
}

object SubscriberTransitions {
  sealed trait SubscriptionState { def name: String }
  case object Unsubscribed extends SubscriptionState { val name = "unsubscribed"}
  case object SelectingLanguage extends SubscriptionState { val name="selecting_language"}
  case object Complete extends SubscriptionState { val name="complete"}

  def withName(value : String): SubscriptionState = value match {
      case Unsubscribed.name => Unsubscribed
      case SelectingLanguage.name => SelectingLanguage
      case Complete.name => Complete
      case _ => Unsubscribed
  }
}
