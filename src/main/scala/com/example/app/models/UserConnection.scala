package com.example.app.models

import com.example.app.{HasIntId, SlickDbObject, Tables}
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserConnection(id: Int = 0, senderUserId: Int, receiverUserId: Int) extends HasIntId[UserConnection]{

  def updateId(id: Int) =
    this.copy(id = id)
}

case class ConnectionRequestJson(addUserId: Int) {
  def newConnection(senderUserId: Int) = {
    UserConnection(
      senderUserId = senderUserId,
      receiverUserId = addUserId
    )
  }
}

case class ConnectionDeleteJson(removeUserId: Int)

object UserConnection extends SlickDbObject[UserConnection, (Int, Int, Int), Tables.UserConnections]{

  lazy val table = Tables.userConnections

  def reify(tuple: (Int, Int, Int)) =
    UserConnection(tuple._1, tuple._2, tuple._3)

  def classToTuple(a: UserConnection) =
    (a.id, a.senderUserId, a.receiverUserId)

  def safeSave(connection: UserConnection) = {
    findConnection(connection.senderUserId, connection.receiverUserId).flatMap(optionalConnection => {
      if (optionalConnection.isEmpty) {
        create(connection)
      } else {
        Future.apply(optionalConnection.get)
      }
    })
  }

  def findConnection(senderUserId: Int, receiverUserId: Int) =
    db.run(table.filter(a => a.senderUserId === senderUserId && a.receiverUserId === receiverUserId).result).map(_.map(reify).headOption)

  def getBySenderId(senderId: Int) =
    db.run(table.filter(_.senderUserId === senderId).result).map(_.map(reify))

  def getReceiversBySenderId(senderId: Int) =
    db.run(
      (for {
        (cs, us) <- table.filter(_.senderUserId === senderId) join Tables.users on (_.receiverUserId === _.id)
      } yield (us)).result).map(_.map(User.reifyJson)
    )

  def getByReceiverId(receiverId: Int) =
    db.run(table.filter(_.receiverUserId === receiverId).result).map(_.map(reify))

  def getSendersByReceiverId(receiverId: Int) =
    db.run(
      (for {
        (cs, us) <- table.filter(_.receiverUserId === receiverId) join Tables.users on (_.senderUserId === _.id)
      } yield (us)).result).map(_.map(User.reifyJson))

  def removeBySenderReceiverPair(senderUserId: Int, receiverUserId: Int) =
    db.run(table.filter(a => a.senderUserId === senderUserId && a.receiverUserId === receiverUserId).delete)
}