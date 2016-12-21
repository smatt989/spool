package com.example.app.models

import com.example.app.Tables.TriggerElements
import com.example.app.{HasIntId, Tables, Updatable}
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.PostgresDriver.api._

/**
  * Created by matt on 12/19/16.
  */
case class TriggerElement(elementType: TriggerElementType, elementSubTypeId: Int, order: Int, triggerId: Int, id: Int) extends HasIntId[TriggerElement] {

  def updateId(id: Int) = this.copy(id = id)
}

object TriggerElement extends Updatable[TriggerElement, (Int, String, Int, Int, Int), TriggerElements] {
  def updateQuery(a: TriggerElement) = table.filter(_.id === a.id)
    .map(x => (x.elementType, x.triggerElementSubTypeId, x.order))
    .update((a.elementType.asString, a.elementSubTypeId, a.order))

  lazy val table = Tables.triggerElements

  def reify(tuple: (Int, String, Int, Int, Int)): TriggerElement =
    TriggerElement(TriggerElementType.byString(tuple._2), tuple._3, tuple._4, tuple._5, tuple._1)

  def classToTuple(a: TriggerElement): (Int, String, Int, Int, Int) =
    (a.id, a.elementType.asString, a.elementSubTypeId, a.order, a.triggerId)

  def deleteDeadElementsForTriggerIds(triggerIds: Seq[Int], activeElementIds: Seq[Int]) =
    db.run(table.filter(a => a.triggerId.inSet(triggerIds) && !a.id.inSet(activeElementIds)).delete)

  def byTriggerIds(triggerIds: Seq[Int]) =
    db.run(table.filter(_.triggerId inSet triggerIds).result).map(_.map(reify))
}