package com.example.app.models

import java.sql.Timestamp
import com.example.app.{HasIntId, SlickDbObject, Tables}
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by matt on 1/27/17.
  */
case class AdventureProgress(id: Int = 0, userId: Int, adventureId: Int, step: Int, finished: Boolean, updatedAt: Long = DateTime.now().getMillis) extends HasIntId[AdventureProgress]{

  def updateId(id: Int) =
    this.copy(id = id)
}

case class ProgressRequest(userId: Int, adventureId: Int)

case class ProgressJsonInsert(step: Int, finished: Boolean = false){
  def newAdventureProgress(userId: Int, adventureId: Int) =
    AdventureProgress(
      userId = userId,
      adventureId = adventureId,
      step = step,
      finished = finished
    )
}

object AdventureProgress extends SlickDbObject[AdventureProgress, (Int, Int, Int, Int, Boolean, Timestamp), Tables.AdventureProgress] {

  lazy val table = Tables.adventureProgress

  def reify(tuple: (Int, Int, Int, Int, Boolean, Timestamp)) =
    AdventureProgress(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6.getTime)

  def classToTuple(a: AdventureProgress) =
    (a.id, a.userId, a.adventureId, a.step, a.finished, new Timestamp(a.updatedAt))

  def getAllLatestProgressesFor(userId: Int) =
    getAllProgressesFor(userId).map(_.mapValues(_.last))

  def getLatestProgress(request: ProgressRequest) =
    getProgresses(request).map(_.lastOption)

  def getAllProgressesFor(userId: Int) =
    db.run(table.filter(_.userId === userId).result).map(_.map(reify).groupBy(_.adventureId).mapValues(_.sortBy(_.updatedAt)))

  def getProgresses(request: ProgressRequest) = {
    db.run(table.filter(a => a.adventureId === request.adventureId && a.userId === request.userId).result)
        .map(_.map(reify).sortBy(_.updatedAt))
  }
}