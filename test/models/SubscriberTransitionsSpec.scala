package models

import org.scalatest.{FunSpec, Matchers}

class SubscriberTransitionsSpec extends FunSpec with Matchers {
  describe("The subscriber transitions") {
    describe ("when a user is unsubscribed") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.Unsubscribed.name);
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it ("should return the basic help message if an unknown message is passed") {
        val (message, newSubscriber) = subscriberTransitions.receiveInput("Hi")
        message shouldEqual "subscribe_help_msg"
        newSubscriber shouldBe None
      }

      it ("should transition the subscriber to selecting language if join is sent") {
        val (message, newSubscriber) = subscriberTransitions.receiveInput("join")
        message shouldEqual "language_selection_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.SelectingLanguage.name
      }
    }
    describe("when a user is choosing a language") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.SelectingLanguage.name);
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it("should return a help message if an unknown language is selected") {
        val (message, newSubscriber) = subscriberTransitions.receiveInput("Hi")
        message shouldEqual "unsupported_lang_msg"
        newSubscriber shouldBe None
      }

      it("should transition the subscriber to completed if a language is selected") {
        val (message, newSubscriber) = subscriberTransitions.receiveInput("1")
        message shouldEqual "confirmation_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.Complete.name
        newSubscriber.get.language shouldEqual Some("eng")
      }
    }

    describe("when a user has completed the subscription process") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.Complete.name);
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it ("should return information on how to unsubscribe or change languages if an unknown message is sent while subscribed") {
        val (message, newSubscriber) = subscriberTransitions.receiveInput("Hi")
        message shouldEqual "error_msg"
        newSubscriber shouldBe None
      }

      it ("should return to the select language state if the appropriate message is sent") {
        val (message, newSubscriber) = subscriberTransitions.receiveInput("change language")
        message shouldEqual "language_selection_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.SelectingLanguage.name
      }

      it ("should unsubscribe the user if the appropriate message is sent") {
        val (message, newSubscriber) = subscriberTransitions.receiveInput("leave")
        message shouldEqual "unsubscribed_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.Unsubscribed.name
      }
    }
  }
}
