package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class SubscriberRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class SubscriberTable(tag: Tag) extends Table[Subscriber](tag, "Subscriber") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def phone = column[String]("phone_number")
    def language = column[Option[String]]("language")
    def state = column[String]("state")

    def * = (id, phone, language, state) <> ((Subscriber.apply _).tupled, Subscriber.unapply)
  }

  private val subscribers = TableQuery[SubscriberTable]

  def create(phone: String): Future[Subscriber] = db.run {
    (subscribers.map(s => (s.phone, s.state))
      returning subscribers.map(_.id)
      into ((row, id) => Subscriber(id, row._1, None, row._2))
    ) += (phone, "unsubscribed")
  }

  def list(): Future[Seq[Subscriber]] = db.run {
    subscribers.result
  }

  def listActive() : Future[Seq[Subscriber]] = db.run {
    subscribers.filter(_.state === "complete").result
  }

  def getOrCreate(phone: String): Future[Subscriber] = {
    val action = subscribers
      .filter(_.phone === phone)
      .result
      .headOption
      .flatMap {
        case Some(subscriber) => DBIO.successful(subscriber)
        case None => (subscribers.map(s => (s.phone, s.state))
            returning subscribers.map(_.id)
            into ((row, id) => Subscriber(id, row._1, None, row._2))
            ) += (phone, "unsubscribed")
      }.transactionally
    db.run(action)
  }

  def update(subscriber: Subscriber): Future[Option[Subscriber]] = db.run {
    subscribers.filter(_.id === subscriber.id).update(subscriber).map {
      case 0 => None
      case _ => Some(subscriber)
    }
  }
}
