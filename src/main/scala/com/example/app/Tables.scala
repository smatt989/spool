package com.example.app

//import slick.driver.H2Driver.api._
import java.sql.Timestamp

import slick.driver.PostgresDriver.api._
import slick.profile.SqlProfile.ColumnOption.SqlType


object Tables {

  class Users(tag: Tag) extends Table[(Int, String, String, String)](tag, "USER_ACCOUNTS") with HasIdColumn[Int] {
    def id = column[Int]("USER_ACCOUNT_ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME")
    def email = column[String]("EMAIL")
    def hashedPassword = column[String]("HASHED_PASSWORD")

    def * = (id, username, email, hashedPassword)
  }

  class DeviceTokens(tag: Tag) extends Table[(Int, Int, Option[String])](tag, "DEVICE_TOKENS") with HasIdColumn[Int] {
    def id = column[Int]("DEVICE_TOKEN_ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def deviceToken = column[Option[String]]("DEVICE_TOKEN")

    def * = (id, userId, deviceToken)

    def user = foreignKey("DEVICE_TOKENS_TO_USER_FK", userId, users)(_.id)
  }

  class UserSessions(tag: Tag) extends Table[(Int, Int, String)](tag, "USER_SESSIONS") with HasIdColumn[Int] {
    def id = column[Int]("USER_SESSION_ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def hashString = column[String]("HASH_STRING")

    def * = (id, userId, hashString)

    def user = foreignKey("USER_SESSIONS_TO_USER_FK", userId, users)(_.id)
  }

  class Adventures(tag: Tag) extends Table[(Int, Int, String, Option[String])](tag, "ADVENTURES") with HasIdColumn[Int] {
    def id = column[Int]("ADVENTURE_ID", O.PrimaryKey, O.AutoInc)
    def creatorUserId = column[Int]("CREATOR_USER_ID")
    def name = column[String]("NAME")
    def description = column[Option[String]]("DESCRIPTION")

    def * = (id, creatorUserId, name, description)

    def user = foreignKey("ADVENTURES_TO_USER_FK", creatorUserId, users)(_.id)
  }

  class Waypoints(tag: Tag) extends Table[(Int, Int, Option[String], Option[String], Boolean, Option[Int], Option[Int], Option[Int], Double, Double, Int)](tag, "WAYPOINTS") with HasIdColumn[Int] {
    def id = column[Int]("WAYPOINT_ID", O.PrimaryKey, O.AutoInc)
    def adventureId = column[Int]("ADVENTURE_ID")
    def name = column[Option[String]]("NAME")
    def description = column[Option[String]]("DESCRIPTION")
    def showDirections = column[Boolean]("SHOW_DIRECTIONS")
    def showBeaconWithinMeterRange = column[Option[Int]]("SHOW_BEACON_WITHIN_METER_RANGE")
    def showNameWithinMeterRange = column[Option[Int]]("SHOW_NAME_WITHIN_METER_RANGE")
    def showDescriptionWithinMeterRange = column[Option[Int]]("SHOW_DESCRIPTION_WITHIN_METER_RANGE")
    def latitude = column[Double]("LATITUDE")
    def longitude = column[Double]("LONGITUDE")
    def order = column[Int]("ORDER_VALUE")

    def * = (id, adventureId, name, description, showDirections, showBeaconWithinMeterRange, showNameWithinMeterRange, showDescriptionWithinMeterRange, latitude, longitude, order)

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

  class Triggers(tag: Tag) extends Table[(Int, String, Int, Int)](tag, "TRIGGERS") with HasIdColumn[Int] {
    def id = column[Int]("TRIGGER_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def adventureId = column[Int]("ADVENTURE_ID")
    def order = column[Int]("ORDER_VALUE")

    def * = (id, name, adventureId, order)

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

  class UserConnections(tag: Tag) extends Table[(Int, Int, Int)](tag, "USER_CONNECTIONS") with HasIdColumn[Int] {
    def id = column[Int]("USER_CONNECTION_ID", O.PrimaryKey, O.AutoInc)
    def senderUserId = column[Int]("SENDER_USER_ID")
    def receiverUserId = column[Int]("RECEIVER_USER_ID")

    def * = (id, senderUserId, receiverUserId)

    def sender = foreignKey("USER_CONNECTIONS_SENDER_TO_USERS_FK", senderUserId, users)(_.id)
    def receiver = foreignKey("USER_CONNECTIONS_RECEIVER_TO_USERS_FK", receiverUserId, users)(_.id)
  }

  class AdventureShares(tag: Tag) extends Table[(Int, Int, Int, Int, Option[String])](tag, "ADVENTURE_SHARES") with HasIdColumn[Int] {
    def id = column[Int]("ADVENTURE_SHARE_ID", O.PrimaryKey, O.AutoInc)
    def senderUserId = column[Int]("SENDER_USER_ID")
    def receiverUserId = column[Int]("RECEIVER_USER_ID")
    def adventureId = column[Int]("ADVENTURE_ID")
    def note = column[Option[String]]("NOTE")

    def * = (id, senderUserId, receiverUserId, adventureId, note)

    def sender = foreignKey("ADVENTURE_SHARES_SENDER_TO_USERS_FK", senderUserId, users)(_.id)
    def receiver = foreignKey("ADVENTURE_SHARES_RECEIVER_TO_USERS_FK", receiverUserId, users)(_.id)
    def adventure = foreignKey("ADVENTURE_SHARE_TO_ADVENTURES_FK", adventureId, adventures)(_.id)
  }

  class AdventureProgress(tag: Tag) extends Table[(Int, Int, Int, Int, Boolean, Timestamp)](tag, "ADVENTURE_PROGRESS") with HasIdColumn[Int] {
    def id = column[Int]("ADVENTURE_PROGRESS_ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def adventureId = column[Int]("ADVENTURE_ID")
    def step = column[Int]("STEP")
    def finished = column[Boolean]("FINISHED")
    def lastUpdated = column[Timestamp]("LAST_UPDATED")

    def * = (id, userId, adventureId, step, finished, lastUpdated)

    def user = foreignKey("ADVENTURE_PROGRESS_TO_USERS_FK", userId, users)(_.id)
    def adventure = foreignKey("ADVENTURE_PROGRESS_TO_ADVENTURES_FK", adventureId, adventures)(_.id)
  }

  val users = TableQuery[Users]
  val deviceTokens = TableQuery[DeviceTokens]
  val userSessions = TableQuery[UserSessions]

  val adventures = TableQuery[Adventures]
  val waypoints = TableQuery[Waypoints]
  val triggerElementSubTypes = TableQuery[TriggerElementSubTypes]
  val triggerElementVariables = TableQuery[TriggerElementVariables]
  val triggers = TableQuery[Triggers]
  val triggerElements = TableQuery[TriggerElements]
  val triggerVariableAssignments = TableQuery[TriggerVariableAssignments]

  val userConnections = TableQuery[UserConnections]
  val adventureShares = TableQuery[AdventureShares]

  val adventureProgress = TableQuery[AdventureProgress]


  val schemas = (users.schema ++ userSessions.schema ++ deviceTokens.schema ++ adventures.schema ++ waypoints.schema ++ triggerElementSubTypes.schema ++
    triggerElementVariables.schema ++ triggers.schema ++ triggerElements.schema ++
    triggerVariableAssignments.schema ++ userConnections.schema ++ adventureShares.schema ++ adventureProgress.schema)


  // DBIO Action which creates the schema
  val createSchemaAction = schemas.create

  // DBIO Action which drops the schema
  val dropSchemaAction = schemas.drop

}

trait HasIdColumn[A]{
  def id: Rep[A]
}
