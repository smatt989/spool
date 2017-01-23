package com.example.app.migrations

import com.example.app.models.Adventure
import com.example.app.{AppGlobals, Tables}
import slick.driver.PostgresDriver.api._

trait Migration {

  def run: Unit
}

class Migration1 extends Migration {

  val removeOldTables = (Tables.triggerVariableAssignments.schema ++ Tables.triggerElements.schema ++ Tables.triggers.schema ++
    Tables.triggerElementVariables.schema ++ Tables.triggerElementSubTypes.schema ++ Tables.waypoints.schema ++
    Tables.adventures.schema)
  val addTables = (Tables.adventures.schema ++ Tables.waypoints.schema ++ Tables.triggerElementSubTypes.schema ++
    Tables.triggerElementVariables.schema ++ Tables.triggers.schema ++ Tables.triggerElements.schema ++
    Tables.triggerVariableAssignments.schema)
  val newTables = (Tables.users.schema ++ Tables.userSessions.schema)

  def run: Unit = {
    println("TRYING")
    AppGlobals.db().run(DBIO.seq(removeOldTables.drop, newTables.create, addTables.create).transactionally)
  }
}

class TestMigration extends Migration {

  def run: Unit = {
    AppGlobals.db().run(DBIO.seq(
      (Tables.waypoints.schema).drop,
      (Tables.waypoints.schema).create
    ).transactionally)
  }
}