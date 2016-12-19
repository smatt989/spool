package com.example.app

//import slick.driver.H2Driver.api._
import slick.driver.PostgresDriver.api._


object Tables {
  class Adventures(tag: Tag) extends Table[(Int, String, Option[String])](tag, "ADVENTURES") with HasIdColumn[Int] {
    def id = column[Int]("ADVENTURE_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def description = column[Option[String]]("DESCRIPTION")

    def * = (id, name, description)
  }

  class Waypoints(tag: Tag) extends Table[(Int, Int, Option[String], Double, Double, Int)](tag, "WAYPOINTS") with HasIdColumn[Int] {
    def id = column[Int]("WAYPOINT_ID", O.PrimaryKey, O.AutoInc)
    def adventureId = column[Int]("ADVENTURE_ID")
    def name = column[Option[String]]("NAME")
    def latitude = column[Double]("LATITUDE")
    def longitude = column[Double]("LONGITUDE")
    def order = column[Int]("ORDER_VALUE")

    def * = (id, adventureId, name, latitude, longitude, order)

    def adventure = foreignKey("WAYPOINTS_TO_ADVENTURE_FK", adventureId, adventures)(_.id)
  }

  class TriggerElementSubTypes(tag: Tag) extends Table[(Int, String, Boolean, String)](tag, "TRIGGER_ELEMENT_SUB_TYPES") with HasIdColumn[Int] {
    def id = column[Int]("TRIGGER_ELEMENT_SUB_TYPE_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def enabled = column[Boolean]("ENABLED")
    def elementType = column[String]("ELEMENT_TYPE")

    def * = (id, name, enabled, elementType)
  }

  class TriggerElementVariables(tag: Tag) extends Table[(Int, Int, String, String, String, Int)](tag, "TRIGGER_ELEMENT_VARIABLES") with HasIdColumn[Int]{
    def id = column[Int]("TRIGGER_ELEMENT_VARIABLE_ID", O.PrimaryKey, O.AutoInc)
    def triggerElementSubTypeId = column[Int]("TRIGGER_ELEMENT_SUB_TYPE_ID")
    def name = column[String]("NAME")
    def variableArity = column[String]("VARIABLE_ARITY")
    def variableType = column[String]("VARIABLE_TYPE")
    def order = column[Int]("ORDER_VALUE")

    def * = (id, triggerElementSubTypeId, name, variableArity, variableType, order)

    def triggerElementType = foreignKey("TRIGGER_ELEMENT_VARIABLES_TO_TRIGGER_ELEMENT_SUB_TYPE_FK", triggerElementSubTypeId, triggerElementSubTypes)(_.id)
  }

  class Triggers(tag: Tag) extends Table[(Int, String, Int)](tag, "TRIGGERS") with HasIdColumn[Int] {
    def id = column[Int]("TRIGGER_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def adventureId = column[Int]("ADVENTURE_ID")

    def * = (id, name, adventureId)

    def adventure = foreignKey("TRIGGERS_TO_ADVENTURE_FK", adventureId, adventures)(_.id)
  }

  class TriggerElements(tag: Tag) extends Table[(Int, String, Int, Int, Int)](tag, "TRIGGER_ELEMENTS") with HasIdColumn[Int] {
    def id = column[Int]("TRIGGER_ELEMENT_ID", O.PrimaryKey, O.AutoInc)
    def elementType = column[String]("ELEMENT_TYPE")
    def triggerElementSubTypeId = column[Int]("TRIGGER_ELEMENT_SUB_TYPE_ID")
    def order = column[Int]("ORDER_VALUE")
    def triggerId = column[Int]("TRIGGER_ID")

    def * = (id, elementType, triggerElementSubTypeId, order, triggerId)

    def triggerElementSubType = foreignKey("TRIGGER_ELEMENTS_TO_TRIGGER_ELEMENT_SUB_TYPE_FK", triggerElementSubTypeId, triggerElementSubTypes)(_.id)
    def trigger = foreignKey("TRIGGER_ELEMENTS_TO_TRIGGER_FK", triggerId, triggers)(_.id)
  }

  class TriggerVariableAssignments(tag: Tag) extends Table[(Int, Int, Int, Int, Option[Int], Option[Int])](tag, "TRIGGER_VARIABLE_ASSIGNMENTS") with HasIdColumn[Int] {
    def id = column[Int]("TRIGGER_VARIABLE_ASSIGNMENT_ID", O.PrimaryKey, O.AutoInc)
    def triggerElementId = column[Int]("TRIGGER_ELEMENT_ID")
    def variableIndex = column[Int]("VARIABLE_INDEX")
    def arrayIndex = column[Int]("ARRAY_INDEX")
    def waypointId = column[Option[Int]]("WAYPOINT_ID")
    def integerValue = column[Option[Int]]("INTEGER_VALUE")

    def * = (id, triggerElementId, variableIndex, arrayIndex, waypointId, integerValue)

    def triggerElement = foreignKey("TRIGGER_VARIABLE_ASSIGNMENTS_TO_TRIGGER_ELEMENT_FK", triggerElementId, triggerElements)(_.id)
    def waypoint = foreignKey("TRIGGER_VARIABLE_ASSIGNMENTS_TO_WAYPOINTS", waypointId, waypoints)(_.id)
  }

  val adventures = TableQuery[Adventures]
  val waypoints = TableQuery[Waypoints]
  val triggerElementSubTypes = TableQuery[TriggerElementSubTypes]
  val triggerElementVariables = TableQuery[TriggerElementVariables]
  val triggers = TableQuery[Triggers]
  val triggerElements = TableQuery[TriggerElements]
  val triggerVariableAssignments = TableQuery[TriggerVariableAssignments]

  val oldSchemas = (adventures.schema ++ waypoints.schema)

  val schemas = (adventures.schema ++ waypoints.schema ++ triggerElementSubTypes.schema ++
    triggerElementVariables.schema ++ triggers.schema ++ triggerElements.schema ++
    triggerVariableAssignments.schema)


  // DBIO Action which creates the schema
  val createSchemaAction = schemas.create

  // DBIO Action which drops the schema
  val dropSchemaAction = schemas.drop

  val dropOldSchemaAction = oldSchemas.drop

}

trait HasIdColumn[A]{
  def id: Rep[A]
}
