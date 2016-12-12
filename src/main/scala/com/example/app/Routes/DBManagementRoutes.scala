package com.example.app.Routes

import com.example.app.{DataImport, SlickRoutes, Tables}
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global


trait DBManagementRoutes extends SlickRoutes{

  get("/db/create-tables") {
    db.run(Tables.createSchemaAction)
  }

  get("/db/drop-tables") {
    db.run(Tables.dropSchemaAction)
  }

  get("/db/drop-old-tables"){
    db.run(Tables.dropOldSchemaAction)
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
