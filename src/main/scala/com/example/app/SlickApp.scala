package com.example.app

import com.example.app.Routes._
import org.scalatra.{FutureSupport, ScalatraServlet}


class SlickApp() extends ScalatraServlet with FutureSupport
  with UserRoutes
  with SessionRoutes
  with AppRoutes
  with DBManagementRoutes
  with AdventureProgressRoutes
  with AdventureShareRoutes {

  def db = AppGlobals.db()

  lazy val realm = "spool"

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

}