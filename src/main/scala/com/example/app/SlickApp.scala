package com.example.app

import com.example.app.Routes.{AppRoutes, DBManagementRoutes}
import org.scalatra.{FutureSupport, ScalatraServlet}


class SlickApp() extends ScalatraServlet with FutureSupport
  with AppRoutes
  with DBManagementRoutes{

  def db = AppGlobals.db()

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

}