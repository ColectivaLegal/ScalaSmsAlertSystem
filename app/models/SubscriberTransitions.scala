package models

import models.SubscriberTransitions.{Complete, SelectingLanguage, SubscriptionState, Unsubscribed}

class SubscriberTransitions(subscriber : Subscriber) {
  def receiveInput(input : String):(String, Option[Subscriber]) = {
    val trimmedInput = input.trim
    val currentstate = SubscriberTransitions.withName(subscriber.state);
    currentstate match {
      case Unsubscribed =>
        if(trimmedInput.equalsIgnoreCase("join")) {
          val newSubscriber = subscriber.copy(state = SelectingLanguage.name)
          return ("language_selection_msg", Some(newSubscriber))
        } else {
          return ("subscribe_help_msg", None)
        }
      case SelectingLanguage =>
        if(trimmedInput.equalsIgnoreCase("1")) {
          val newSubscriber = subscriber.copy(language = Some("eng"), state=Complete.name)
          return ("confirmation_msg", Some(newSubscriber))
        } else {
          return ("unsupported_lang_msg", None)
        }
      case Complete =>
        if (trimmedInput.equalsIgnoreCase("change language")) {
          val newSubscriber = subscriber.copy(state = SelectingLanguage.name)
          return ("language_selection_msg", Some(newSubscriber))
        } else if (trimmedInput.equalsIgnoreCase("leave")) {
          val newSubscriber = subscriber.copy(state = Unsubscribed.name)
          return ("unsubscribed_msg", Some(newSubscriber))
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
