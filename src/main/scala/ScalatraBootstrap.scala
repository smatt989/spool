import com.example.app._
import org.scalatra._
import javax.servlet.ServletContext
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.slf4j.LoggerFactory
import slick.driver.H2Driver.api._

class ScalatraBootstrap extends LifeCycle {
  val logger = LoggerFactory.getLogger(getClass)

  val cpds = new ComboPooledDataSource
  logger.info("Created c3p0 connection pool")

  override def init(context: ServletContext): Unit = {
    if(isProduction(context)){
      val DRIVER = "org.postgresql.Driver"
      val DB_CONNECTION = System.getenv("JDBC_DATABASE_URL")
      val DB_USER = System.getenv("JDBC_DATABASE_USERNAME")
      val DB_PASSWORD = System.getenv("JDBC_DATABASE_PASSWORD")
      cpds.setDriverClass(DRIVER)
      cpds.setJdbcUrl(DB_CONNECTION)
      cpds.setUser(DB_USER)
      cpds.setPassword(DB_PASSWORD)
    }

    System.out.print("TRYING TO CONNECTION TO DB NOW...")
    System.out.print("CONNECTION: "+cpds.getJdbcUrl)
    System.out.print("USER: "+cpds.getUser)
    val db = Database.forDataSource(cpds)
    System.out.print("OK CONNECTED DB...")
    AppGlobals.db = () => db

    context.mount(new SlickApp(), "/*")

  }

  private[this] def isProduction(context: ServletContext) = {
    val envKey = context.environment
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
