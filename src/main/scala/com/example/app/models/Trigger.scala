package com.example.app.models

import com.example.app.Tables.Triggers
import com.example.app.{HasIntId, Tables, Updatable}
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.PostgresDriver.api._

case class Trigger(name: String, adventureId: Int, order: Int, id: Int) extends HasIntId[Trigger] {
  def updateId(id: Int) = this.copy(id = id)
}

object Trigger extends Updatable[Trigger, (Int, String, Int, Int), Triggers]{
  def updateQuery(a: Trigger) = table.filter(_.id === a.id)
    .map(x => (x.name, x.order))
    .update((a.name, a.order))

  lazy val table = Tables.triggers

  def reify(tuple: (Int, String, Int, Int)): Trigger =
    Trigger(tuple._2, tuple._3, tuple._4, tuple._1)

  def classToTuple(a: Trigger): (Int, String, Int, Int) =
    (a.id, a.name, a.adventureId, a.order)

  def byAdventureId(adventureId: Int) =
    db.run(table.filter(_.adventureId === adventureId).result).map(_.map(reify))

  def deleteUnactiveTriggersForAdventure(adventureId: Int, activeIds: Seq[Int]) =
    db.run(table.filter(a => a.adventureId === adventureId && !a.id.inSet(activeIds)).delete)
}