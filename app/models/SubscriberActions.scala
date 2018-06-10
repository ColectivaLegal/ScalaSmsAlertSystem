package models

/**
  * SubscriberAction
  */
sealed trait SubscriberActions {
  def join: String
  def changeLanguage: String
  def leave: String
}

object SubscriberActions {
  val EnglishActions: SubscriberActions = new SubscriberActions() {
    override def join: String = "join"
    override def changeLanguage: String = "change language"
    override def leave: String = "leave"
  }

  val SpanishActions: SubscriberActions = new SubscriberActions() {
    override def join: String = "suscribirse"
    override def changeLanguage: String = "cambio de lengua"
    override def leave: String = "abandonar"
  }

  val KoreanActions: SubscriberActions = new SubscriberActions() {
    override def join: String = "등록"
    override def changeLanguage: String = "언어변경"
    override def leave: String = "탈퇴"
  }

  val ChineseActions: SubscriberActions = new SubscriberActions() {
    override def join: String = "加入"
    override def changeLanguage: String = "改變語言"
    override def leave: String = "離開"
  }

  val VietnameseActions: SubscriberActions = new SubscriberActions() {
    override def join: String = "Tham gia"
    override def changeLanguage: String = "Thay đổi ngôn ngữ"
    override def leave: String = "Rời khỏi"
  }

  val AllLanguageActions: Array[SubscriberActions] =
    Array(EnglishActions, SpanishActions, KoreanActions, ChineseActions, VietnameseActions)

  def isJoinAction(text: String): Boolean = AllLanguageActions.exists(action => action.join.equalsIgnoreCase(text))

  def isChangeLanguageAction(text: String): Boolean = AllLanguageActions.exists(action => action.changeLanguage.equalsIgnoreCase(text))

  def isLeaveAction(text: String): Boolean = AllLanguageActions.exists(action => action.leave.equalsIgnoreCase(text))
}

