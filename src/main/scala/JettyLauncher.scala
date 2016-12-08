import com.example.app.SlickApp
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyLauncher {
  val PRODUCTION = "production"

  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)
    val context = new WebAppContext()
    context.setContextPath("/")
    context.setResourceBase("src/main/webapp")
    context.setInitParameter(org.scalatra.EnvironmentKey, PRODUCTION)
    context.addServlet(classOf[SlickApp], "/*")

    context.setEventListeners(Array(new ScalatraListener))

    server.setHandler(context)

    server.start
    server.join
  }
}