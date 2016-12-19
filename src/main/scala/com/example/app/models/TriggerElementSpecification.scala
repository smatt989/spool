package com.example.app.models

import scala.concurrent.ExecutionContext.Implicits.global

case class TriggerElementSpecification(title: String, enabled: Boolean, elementType: TriggerElementType, variables: Seq[TriggerElementVariableSpec], id: Int){

  def toRow = TriggerElementSubType(title, enabled, elementType, id)

  lazy val toJson = JsonTriggerElementSpecification(title, enabled, elementType.asString, variables.map(_.toJson), id)
}

case class JsonTriggerElementSpecification(title: String, enabled: Boolean, elementType: String, variables: Seq[JsonTriggerElementVariableSpec], id: Int)

case class JsonTriggerSpecification(actions: Seq[JsonTriggerElementSpecification], events: Seq[JsonTriggerElementSpecification])

object TriggerElementSpecification {

  def getAll = {
    TriggerElementSubType.getAll.flatMap(es => {
      TriggerElementVariableSpec.getAll.map(vs => {
        val byElementId = vs.groupBy(_.triggerElementSubTypeId)
        es.map(e => {
          TriggerElementSpecification(e.name, e.enabled, e.elementType, byElementId.get(e.id).getOrElse(Nil).sortBy(_.order), e.id)
        })
      })
    })
  }

  val events = Seq(
    TriggerElementSpecification("adventure starts", true, TriggerEvent, Nil, 0),
    TriggerElementSpecification("time of day changes", false, TriggerEvent, Nil, 0),
    TriggerElementSpecification("player location changes", false, TriggerEvent, Nil, 0),
    TriggerElementSpecification("player enters location", false, TriggerEvent,
      Seq(TriggerElementVariableSpec(0, "location", ArityOne, TypeObject, 0, 0)), 0),
    TriggerElementSpecification("player exits location", false, TriggerEvent,
      Seq(TriggerElementVariableSpec(0, "location", ArityOne, TypeObject, 0, 0)), 0),
    TriggerElementSpecification("periodic", true, TriggerEvent,
      Seq(TriggerElementVariableSpec(0, "seconds", ArityOne, TypeInteger, 0, 0)), 0)
  )

  val actions = Seq(
    TriggerElementSpecification("direct player to location", true, TriggerAction,
      Seq(TriggerElementVariableSpec(0, "destination", ArityOne, TypeObject, 0, 0)), 0),
    TriggerElementSpecification("direct player to location with ordered waypoints", true, TriggerAction,
      Seq(TriggerElementVariableSpec(0, "destination", ArityOne, TypeObject, 0, 0),
        TriggerElementVariableSpec(0, "waypoints", ArityArray, TypeObject, 1, 0)), 0),
    TriggerElementSpecification("direct player to location with waypoints optimized", true, TriggerAction,
      Seq(TriggerElementVariableSpec(0, "destination", ArityOne, TypeObject, 0, 0),
        TriggerElementVariableSpec(0, "waypoints", ArityArray, TypeObject, 1, 0)), 0),
    TriggerElementSpecification("pause directions", false, TriggerAction, Nil, 0),
    TriggerElementSpecification("resume directions", false, TriggerAction, Nil, 0),
    TriggerElementSpecification("end directions", false, TriggerAction, Nil, 0),
    TriggerElementSpecification("move object to", false, TriggerAction,
      Seq(TriggerElementVariableSpec(0, "object", ArityOne, TypeObject, 0, 0),
        TriggerElementVariableSpec(0, "location", ArityOne, TypeObject, 1, 0)), 0),
    TriggerElementSpecification("make object visible", false, TriggerAction,
      Seq(TriggerElementVariableSpec(0, "object", ArityOne, TypeObject, 0, 0)), 0),
    TriggerElementSpecification("make object hidden", false, TriggerAction,
      Seq(TriggerElementVariableSpec(0, "object", ArityOne, TypeObject, 0, 0)), 0),
    TriggerElementSpecification("end adventure", true, TriggerAction, Nil, 0),
    TriggerElementSpecification("victory", true, TriggerAction, Nil, 0),
    TriggerElementSpecification("defeat", true, TriggerAction, Nil, 0)
  )

  def saveItem(item: TriggerElementSpecification) = {
    val savedElement = TriggerElementSubType.create(item.toRow)
    savedElement.flatMap(se => {
      val toSave = item.variables.map(v => v.copy(triggerElementSubTypeId = se.id))
      TriggerElementVariableSpec.createMany(toSave)
    })
  }

  def saveAll =
    (events ++ actions).map(saveItem)
}