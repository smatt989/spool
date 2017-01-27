package com.example.app.models

import com.example.app.{HasIntId, SlickDbObject, Tables}
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by matt on 1/26/17.
  */
case class AdventureShare(id: Int, senderUserId: Int, receiverUserId: Int, adventureId: Int, note: Option[String] = None) extends HasIntId[AdventureShare] {

  def updateId(id: Int) =
    this.copy(id = id)
}

case class ShareAdventureJsonRequest(receiverUserId: Int, adventureId: Int, note: Option[String] = None) {
  def newAdventureShare(senderUserId: Int) =
    AdventureShare(0, senderUserId, receiverUserId, adventureId, note)
}

case class AdventureShareJson(sender: UserJson, receiver: UserJson, adventure: AdventureJson, note: Option[String] = None)

object AdventureShare extends SlickDbObject[AdventureShare, (Int, Int, Int, Int, Option[String]), Tables.AdventureShares] {

  lazy val table = Tables.adventureShares

  def reify(tuple: (Int, Int, Int, Int, Option[String])) =
    AdventureShare(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5)

  def classToTuple(a: AdventureShare) =
    (a.id, a.senderUserId, a.receiverUserId, a.adventureId, a.note)

  def safeSave(share: AdventureShare) = {
    safeSaveManyForOneSender(Seq(share), share.senderUserId)
  }

  def safeSaveManyForOneSender(shares: Seq[AdventureShare], senderId: Int) = {
    adventureSharedBySenderIdRows(senderId).flatMap(alreadySaved => {
      createMany(shares).flatMap(newlySaved => {
          val alreadySavedByTriple = alreadySaved.map(a => (a.senderUserId, a.receiverUserId, a.adventureId) -> a).toMap
          val toDelete = newlySaved.flatMap(a => alreadySavedByTriple.get(a.senderUserId, a.receiverUserId, a.adventureId))
          deleteMany(toDelete.map(_.id)).map(_ => newlySaved)
      })
    })
  }

  def adventureSharedBySenderIdRows(senderUserId: Int) = {
    db.run(table.filter(_.senderUserId === senderUserId).result).map(_.map(reify))
  }

  def adventuresSharedBySenderId(senderUserId: Int) = {
    db.run(
      (for {
        shares <- table.filter(_.senderUserId === senderUserId)
        adventures <- Adventure.table if adventures.id === shares.adventureId
        creators <- User.table if creators.id === adventures.creatorUserId
        receivers <- User.table if receivers.id === shares.receiverUserId
        senders <- User.table if senders.id === shares.senderUserId
      } yield (adventures, creators, receivers, senders, shares)).result).map(_.map(a => {
      AdventureShareJson(
        sender = User.reifyJson(a._4),
        receiver = User.reifyJson(a._3),
        adventure = Adventure.reify(a._1).toJson(User.reifyJson(a._2)),
        note = a._5._5)
    }))
  }

  def adventuresSharedByReceiverId(receiverUserId: Int) =
    db.run(
      (for {
        shares <- table.filter(_.receiverUserId === receiverUserId)
        adventures <- Adventure.table if adventures.id === shares.adventureId
        creators <- User.table if creators.id === adventures.creatorUserId
        receivers <- User.table if receivers.id === shares.receiverUserId
        senders <- User.table if senders.id === shares.senderUserId
      } yield (adventures, creators, receivers, senders, shares)).result).map(_.map(a => {
      AdventureShareJson(
        sender = User.reifyJson(a._4),
        receiver = User.reifyJson(a._3),
        adventure = Adventure.reify(a._1).toJson(User.reifyJson(a._2)),
        note = a._5._5)
    }))
}