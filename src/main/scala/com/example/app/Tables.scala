package com.example.app

import slick.driver.H2Driver.api._


object Tables {

  class Users(tag: Tag) extends Table[(Int, String, String, String)](tag, "USER_ACCOUNTS") with HasIdColumn[Int] {
    def id = column[Int]("USER_ACCOUNT_ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME")
    def email = column[String]("EMAIL")
    def hashedPassword = column[String]("HASHED_PASSWORD")

    def * = (id, username, email, hashedPassword)
  }

  class UserSessions(tag: Tag) extends Table[(Int, Int, String)](tag, "USER_SESSIONS") with HasIdColumn[Int] {
    def id = column[Int]("USER_SESSION_ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def hashString = column[String]("HASH_STRING")

    def * = (id, userId, hashString)

    def user = foreignKey("USER_SESSIONS_TO_USER_FK", userId, users)(_.id)
  }

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

  val users = TableQuery[Users]
  val userSessions = TableQuery[UserSessions]
  val adventures = TableQuery[Adventures]
  val waypoints = TableQuery[Waypoints]

  val schemas = (adventures.schema ++ waypoints.schema ++ users.schema ++ userSessions.schema)


  // DBIO Action which creates the schema
  val createSchemaAction = schemas.create

  // DBIO Action which drops the schema
  val dropSchemaAction = schemas.drop

}

trait HasIdColumn[A]{
  def id: Rep[A]
}
