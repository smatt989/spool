package com.example.app.models

import com.example.app.Tables.TriggerVariableAssignments
import com.example.app.{HasIntId, Tables, Updatable}
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class TriggerVariableAssignment(triggerElementId: Int, variableIndex: Int, arrayIndex: Int, waypointId: Option[Int], integerValue: Option[Int], id: Int) extends HasIntId[TriggerVariableAssignment]{
  def updateId(id: Int) = this.copy(id = id)
}

object TriggerVariableAssignment extends Updatable[TriggerVariableAssignment, (Int, Int, Int, Int, Option[Int], Option[Int]), TriggerVariableAssignments]{
  def updateQuery(a: TriggerVariableAssignment) = table.filter(_.id === a.id)
    .map(x => (x.waypointId, x.integerValue))
    .update((a.waypointId, a.integerValue))


  lazy val table = Tables.triggerVariableAssignments

  def reify(tuple: (Int, Int, Int, Int, Option[Int], Option[Int])) =
    TriggerVariableAssignment(tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._1)

  def classToTuple(a: TriggerVariableAssignment) =
    (a.id, a.triggerElementId, a.variableIndex, a.arrayIndex, a.waypointId, a.integerValue)

  def deleteByTriggerElementIds(ids: Seq[Int]) =
    db.run(table.filter(_.triggerElementId inSet ids).delete)

  def byTriggerElementIds(ids: Seq[Int]) =
    db.run(table.filter(_.triggerElementId inSet ids).result).map(_.map(reify))
}