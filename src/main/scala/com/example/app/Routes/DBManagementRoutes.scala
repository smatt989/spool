package com.example.app.Routes

import com.example.app.{DataImport, SlickRoutes, Tables}
import slick.driver.H2Driver.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


trait DBManagementRoutes extends SlickRoutes{

  get("/db/create-tables") {
    System.out.println("TRYING TO HIT DB")
    val k = Await.result(db.run(Tables.createSchemaAction), Duration.fromNanos(20000000000L))
    System.out.println("FINISHED")
    k
  }

  get("/db/drop-tables") {
    db.run(Tables.dropSchemaAction)
  }

  get("/db/load-data") {
    DataImport.populateData(db)
  }

  get("/db/reset"){
    db.run(DBIO.seq(Tables.dropSchemaAction, Tables.createSchemaAction)).foreach { a =>
      DataImport.populateData(db)
    }
  }


}
