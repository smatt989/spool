package com.example.app

import com.example.app.Routes.{AppRoutes, DBManagementRoutes, SessionRoutes, UserRoutes}
import org.scalatra.{FutureSupport, ScalatraServlet}


class SlickApp() extends ScalatraServlet with FutureSupport
  with UserRoutes
  with SessionRoutes
  with AppRoutes
  with DBManagementRoutes{

  lazy val realm = "Spool"

  def db = AppGlobals.db()

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global
}