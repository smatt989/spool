package com.example.app.models

import scala.concurrent.ExecutionContext.Implicits.global

case class FullAdventure(id: Int = 0,
                         name: String = Adventure.randomizeName,
                         description: Option[String] = None,
                         waypoints: Seq[Waypoint],
                         triggers: Seq[FullTrigger]) {

  def dbRow(creatorUserId: Int) = Adventure(id, creatorUserId, name, description)

  def toJson = JsonAdventure(name, description, waypoints.map(_.toJson), triggers.map(_.toJson), id)
}

case class FullTrigger(name: String, event: FullTriggerElement, actions: Seq[FullTriggerElement], id: Int = 0) {
  def dbRow(adventureId: Int, order: Int) = Trigger(name, adventureId, order, id)

  def toJson = JsonTrigger(name, event.toJson, actions.map(_.toJson), id)
}

case class FullTriggerElement(elementType: TriggerElementType, elementSubTypeId: Int, variableAssignments: Seq[Seq[SimpleTriggerElementVariableAssignment]], id: Int) {
  def dbRow(triggerId: Int, order: Int) = TriggerElement(elementType, elementSubTypeId, order, triggerId, id)
  def variableAssignmentRows = variableAssignments.zipWithIndex.map{
    case (assignments, variableIndex) => assignments.zipWithIndex.map{
      case (assignment, arrayIndex) =>
        TriggerVariableAssignment(id, variableIndex, arrayIndex, assignment.objectId, assignment.integerValue, 0)
  }}

  def toJson = JsonTriggerElement(elementSubTypeId, variableAssignments.map(s => SimpleTriggerElementVariableAssignmentList(s)), id)
}

case class SimpleTriggerElementVariableAssignment(objectId: Option[Int] = None, integerValue: Option[Int] = None, objectKey: Option[String])
case class SimpleTriggerElementVariableAssignmentList(varAssignment: Seq[SimpleTriggerElementVariableAssignment])

case class JsonAdventure(name: String = Adventure.randomizeName, description: Option[String] = None, markers: Seq[JsonWaypoint], triggers: Seq[JsonTrigger], id: Int = 0){
  def toModel = FullAdventure(id, name, description, markers.zipWithIndex.map{case (w, i) => w.toModel(id, i)}, triggers.map(_.toModel))
}
case class JsonTrigger(title: String, event: JsonTriggerElement, actions: Seq[JsonTriggerElement], id: Int = 0){
  def toModel = FullTrigger(title, event.toModel(TriggerEvent), actions.map(_.toModel(TriggerAction)), id)
}
case class JsonTriggerElement(itemSubTypeId: Int, varAssignments: Seq[SimpleTriggerElementVariableAssignmentList], id: Int = 0) {
  def toModel(elementType: TriggerElementType) = FullTriggerElement(elementType, itemSubTypeId, varAssignments.map(_.varAssignment), id)
}

object FullAdventure {

  //TODO: NOT GREAT.

  def save(a: FullAdventure, user: UserJson) = {
    val adventure = saveAdventure(a, user)

    adventure.flatMap(adv => {
      val futureWaypoints = saveWaypoints(adv.waypoints, adv.id)
      val futureWaypointIdByKey = futureWaypoints.map(waypoints => waypoints.zipWithIndex.map{case (waypoint, index) => adv.waypoints(index).key -> waypoint.id}.filter(_._1.isDefined).map(a => a._1.get -> a._2).toMap)
      val futureTriggers = saveTriggers(adv.triggers, adv.id)
      futureWaypointIdByKey.flatMap(waypointIdByKey => {
        futureTriggers.flatMap(triggers => {
          val futureTriggersWithElements = saveElements(triggers)
          val futureSavedVariables = futureTriggersWithElements.flatMap(ts => saveVariableAssignments(ts, waypointIdByKey))
          val allOldWaypoints = Waypoint.byAdventureId(adv.id)
          val allOldTriggers = Trigger.byAdventureId(adv.id)
          val allOldTriggerElements = allOldTriggers.flatMap(aot => TriggerElement.byTriggerIds(aot.map(_.id)))
          val allOldAssignments = allOldTriggerElements.flatMap(aote => TriggerVariableAssignment.byTriggerElementIds(aote.map(_.id)))
          futureWaypoints.flatMap(waypoints => {
            futureTriggersWithElements.flatMap(triggersWithElements => {
              futureSavedVariables.flatMap(savedVariables => {
                allOldWaypoints.flatMap(oldWaypoints => {
                  allOldTriggers.flatMap(oldTriggers => {
                    allOldTriggerElements.flatMap(oldTriggerElements => {
                      allOldAssignments.map(oldAssignments => {

                        val waypointsToDelete = oldWaypoints.map(_.id).toSet diff waypoints.map(_.id).toSet
                        val triggersToDelete = oldTriggers.map(_.id).toSet diff triggersWithElements.map(_.id).toSet
                        val elementsToDelete = oldTriggerElements.map(_.id).toSet diff triggersWithElements.flatMap(a => a.event.id +: a.actions.map(_.id)).toSet
                        val assignmentsToDelete = oldAssignments.map(_.id).toSet diff savedVariables.map(_.id).toSet
                        TriggerVariableAssignment.deleteMany(assignmentsToDelete.toSeq).onSuccess { case a =>
                          TriggerElement.deleteMany(elementsToDelete.toSeq).onSuccess { case a =>
                            Trigger.deleteMany(triggersToDelete.toSeq).onSuccess { case a =>
                              Waypoint.deleteMany(waypointsToDelete.toSeq)
                            }
                          }
                        }
                      })
                    })
                  })
                })
              })
            })
          })
        })
      }).map(a => adv.id)
    })
  }

  def getById(id: Int) = {
    val adventureFuture = Adventure.byId(id)
    val triggersFuture = adventureFuture.flatMap(adv => {
      Trigger.byAdventureId(adv.id)
    })
    val waypointsFuture = adventureFuture.flatMap(adv => {
      Waypoint.byAdventureId(adv.id)
    })
    val elementsFuture = triggersFuture.flatMap(triggers => {
      TriggerElement.byTriggerIds(triggers.map(_.id))
    })
    val variablesFuture = elementsFuture.flatMap(elements => {
      TriggerVariableAssignment.byTriggerElementIds(elements.map(_.id))
    })

    adventureFuture.flatMap(adventure => {
      waypointsFuture.flatMap(waypoints => {
        triggersFuture.flatMap(triggers => {
          elementsFuture.flatMap(elements => {
            variablesFuture.map(variables => {
              val triggersSorted = triggers.sortBy(_.order)
              val variableMap = variables.groupBy(a => a.triggerElementId).mapValues(vs => vs.groupBy(_.variableIndex).mapValues(_.sortBy(_.arrayIndex)).toSeq.sortBy(_._1).map(_._2))
              val elementsMap = elements.groupBy(a => (a.triggerId, a.elementType)).mapValues(_.sortBy(_.order))
              FullAdventure(
                adventure.id,
                adventure.name,
                adventure.description,
                waypoints,
                triggersSorted.map(t => {
                  FullTrigger(t.name,
                    elementByRowAndVariableMap(elementsMap(t.id, TriggerEvent).head, variableMap),
                    elementsMap(t.id, TriggerAction).map(e => elementByRowAndVariableMap(e, variableMap)),
                    t.id
                  )
                })
              )
            })
          })
        })
      })
    })

  }

  def elementByRowAndVariableMap(row: TriggerElement, varMap: Map[Int, Seq[Seq[TriggerVariableAssignment]]]) ={
    FullTriggerElement(row.elementType, row.elementSubTypeId, varMap.getOrElse(row.id, Nil).map(_.map(a => SimpleTriggerElementVariableAssignment(a.waypointId, a.integerValue, None))), row.id)
  }

  def saveAdventure(adventure: FullAdventure, user: UserJson) = {
    val toSave = adventure.dbRow(user.id)
    val futureSaved = Adventure.save(toSave)
    futureSaved.map(saved => adventure.copy(id = saved.id))
  }

  def saveTriggers(orderedTriggers: Seq[FullTrigger], adventureId: Int) ={
    val toSave = orderedTriggers.zipWithIndex.map{ case (trigger, index) => trigger.dbRow(adventureId, index)}
    val futureSaved = Trigger.saveMany(toSave)
    futureSaved.map(saved => {
      //Trigger.deleteUnactiveTriggersForAdventure(adventureId, saved.map(_.id))
      saved.zipWithIndex.map{ case (trigger, index) => orderedTriggers(index).copy(id = trigger.id)}
    })
  }

  def saveWaypoints(orderedWaypoints: Seq[Waypoint], adventureId: Int) = {
    val toSave = orderedWaypoints.map(_.copy(adventureId = adventureId))
    Waypoint.saveMany(toSave)
  }

  def saveElements(orderedTriggers: Seq[FullTrigger]) = {
    val toSave = orderedTriggers.flatMap(t => t.actions.zipWithIndex.map{ case (action, index) => action.dbRow(t.id, index) }) ++
      orderedTriggers.map(t => t.event.dbRow(t.id, 0))
    val futureSaved = TriggerElement.saveMany(toSave)
    futureSaved.map(saved => {
      //TriggerElement.deleteDeadElementsForTriggerIds(orderedTriggers.map(_.id), saved.map(_.id))
      val grouped = saved.groupBy(x => (x.triggerId, x.elementType)).mapValues(_.sortBy(_.order))
      orderedTriggers.map(t => {
        val actions = grouped(t.id, TriggerAction)
        val event = grouped(t.id, TriggerEvent)
        val newActions = t.actions.zipWithIndex.map{ case (action, index) => action.copy(id = actions(index).id)}
        val newEvent = t.event.copy(id = event.head.id)
        t.copy(actions = newActions, event = newEvent)
      })
    })
  }

  def saveVariableAssignments(orderedTriggers: Seq[FullTrigger], waypointIdByKey: Map[String, Int]) = {
    val toSave = orderedTriggers.flatMap(t => {
      (t.event +: t.actions).flatMap(action => {
        action.variableAssignments.zipWithIndex.flatMap{
          case (assignments, variableIndex) =>
            assignments.zipWithIndex.map{
              case (assignment, arrayIndex) =>
                val objectId = if(assignment.objectKey.isDefined)
                  assignment.objectKey.map(ok => waypointIdByKey(ok))
                else
                  assignment.objectId
                TriggerVariableAssignment(action.id, variableIndex, arrayIndex, objectId, assignment.integerValue, 0)
          }}
      })
    })
    val deleting = TriggerVariableAssignment.deleteByTriggerElementIds(orderedTriggers.flatMap(t => (t.event +: t.actions).map(_.id)))
    deleting.flatMap(d => TriggerVariableAssignment.saveMany(toSave))
  }

}
