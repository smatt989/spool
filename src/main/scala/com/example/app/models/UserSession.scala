package com.example.app.models

import java.util.UUID

import com.example.app.{HasIntId, SlickDbObject, Tables}
import slick.driver.PostgresDriver.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


case class UserSession(userId: Int, hashString: String, id: Int) extends HasIntId[UserSession]{

  def updateId(id: Int) =
    this.copy(id = id)

  lazy val user =
    Await.result(UserSession.user(this), UserSession.waitDuration)
}

object UserSession extends SlickDbObject[UserSession, (Int, Int, String), Tables.UserSessions]{

  lazy val waitDuration = Duration.fromNanos(10000000000L)

  lazy val table = Tables.userSessions

  def fromUser(userId: Int) =
    Await.result(db.run(table.filter(_.userId === userId).result).map(_.headOption.map(reify)), waitDuration)

  def findFromUserOrCreate(userId: Int) = {
    fromUser(userId).getOrElse(
      Await.result(create(UserSession(userId, UUID.randomUUID().toString, 0)), waitDuration)
    )
  }

  def reify(tuple: (Int, Int, String)) =
    UserSession(tuple._2, tuple._3, tuple._1)

  def classToTuple(a: UserSession) =
    (a.id, a.userId, a.hashString)

  def user(userSession: UserSession) =
    User.byId(userSession.userId)

  def byHashString(hashString: String) =
    Await.result(db.run(table.filter(_.hashString === hashString).result).map(_.headOption.map(reify)), waitDuration)

}