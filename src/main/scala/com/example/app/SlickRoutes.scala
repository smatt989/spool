package com.example.app

import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase}
import slick.driver.H2Driver.api._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.scalatra.scalate.ScalateSupport

trait SlickRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with ScalateSupport with CorsSupport{

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database

//  options("/*"){
//    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
//  }


}