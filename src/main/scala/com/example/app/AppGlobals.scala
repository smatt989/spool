package com.example.app

//import slick.driver.H2Driver.api._
import slick.driver.PostgresDriver.api._

object AppGlobals {

  var db: () => Database = null

  val googleMapsKey = System.getenv("GOOGLE_API_KEY")
}
