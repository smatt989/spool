import com.example.app._
import org.scalatra._
import javax.servlet.ServletContext
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.slf4j.LoggerFactory
// import slick.driver.H2Driver.api._
import slick.driver.PostgresDriver.api._

class ScalatraBootstrap extends LifeCycle {
  val logger = LoggerFactory.getLogger(getClass)

  val cpds = new ComboPooledDataSource

  val cpos = new ComboPooledDataSource()
  logger.info("Created c3p0 connection pool")

  override def init(context: ServletContext): Unit = {
    val db = if(isProduction(context)){
      logger.info("YOU ARE IN PRODUCTION")
      val DB_CONNECTION = System.getenv("JDBC_DATABASE_URL")
      logger.info("CONNECTING TO: "+DB_CONNECTION)
      Database.forURL(DB_CONNECTION)
    } else {
      logger.info("YOU ARE IN DEV")
      Database.forDataSource(cpds)
    }

    AppGlobals.db = () => db

    context.mount(new SlickApp(), "/*")

  }

  private[this] def isProduction(context: ServletContext) = {
    val envKey = context.getInitParameter(org.scalatra.EnvironmentKey)
    envKey != null && envKey == JettyLauncher.PRODUCTION
  }

  private def closeDbConnection() {
  	logger.info("Closing c3p0 connection pool")
  	cpds.close
  }

  override def destroy(context: ServletContext) {
  	super.destroy(context)
  	closeDbConnection
  }
}
