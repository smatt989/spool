package com.example.app

import com.example.app.Routes.{AppRoutes, DBManagementRoutes}
import org.scalatra.{FutureSupport, ScalatraServlet}
import slick.driver.H2Driver.api._

/**
  * Created by matt on 11/22/16.
  */

class SlickApp() extends ScalatraServlet with FutureSupport
  with AppRoutes
  with DBManagementRoutes{

  def db = AppGlobals.db()

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

}