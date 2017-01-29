package com.example.app.models

import com.example.app.{HasIntId, Tables, Updatable}
import org.mindrot.jbcrypt.BCrypt
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

case class User(username: String, email: String, hashedPassword: String, id: Int) extends HasIntId[User] {

  def updateId(id: Int) =
    this.copy(id = id)

  lazy val toJson =
    UserJson(username, id)
}

case class UserCreate(username: String, email: String, password: String) {
  lazy val makeUser =
    User(username, email, User.makeHash(password), 0)
}

case class UpdateUser(username: Option[String], email: Option[String], password: String, newEmail: Option[String], newPassword: Option[String]){
  lazy val userLogin =
    UserLogin(username, email, password)
}

case class UserLogin(username: Option[String], email: Option[String], password: String){
  require({(username ++ email).nonEmpty}, "Must provide either a username or email")

}

case class UserJson(username: String, id: Int)

object User extends Updatable[User, (Int, String, String, String), Tables.Users]{

  lazy val table = Tables.users

  def reify(tuple: (Int, String, String, String)) =
    User(tuple._2, tuple._3, tuple._4, tuple._1)

  def reifyJson(tuple: (Int, String, String, String)) =
    reify(tuple).toJson

  def classToTuple(a: User) =
    (a.id, a.username, a.email, a.hashedPassword)

  def updateQuery(a: User) = table.filter(_.id === a.id)
    .map(x => (x.username, x.email, x.hashedPassword))
    .update((a.username, a.email, a.hashedPassword))

  def makeHash(password: String) =
    BCrypt.hashpw(password, BCrypt.gensalt())

  private[this] def checkPassword(password: String, hashedPassword: String) =
    BCrypt.checkpw(password, hashedPassword)

  def authenticate(user: User, password: String) = {
    checkPassword(password, user.hashedPassword)
  }

  def searchUserName(query: String) = {
    val queryString = "%"+query.toLowerCase()+"%"
    db.run(table.filter(_.username.toLowerCase like queryString).result).map(_.map(reifyJson))
  }

  private[this] def unauthenticatedUserFromUserLogin(userLogin: UserLogin) = {

    Await.result({if(userLogin.username.isDefined)
      db.run(table.filter(_.username.toLowerCase === userLogin.username.get.toLowerCase()).result).map(_.headOption.map(reify).getOrElse{
        throw new Exception("No user with that username")
      })
    else if(userLogin.email.isDefined)
      db.run(table.filter(_.email.toLowerCase === userLogin.email.get.toLowerCase()).result).map(_.headOption.map(reify).getOrElse{
        throw new Exception("No user with that email")
      })
    else
      throw new Exception("Must provide either a username or email")
    }, UserSession.waitDuration)
  }

  def authenticatedUser(userLogin: UserLogin) = {
    val user = unauthenticatedUserFromUserLogin(userLogin)

    if(authenticate(user, userLogin.password))
      Some(user)
    else
      None
  }

  def uniqueUsername(username: String) =
    db.run(table.filter(_.username === username).result).map(_.isEmpty)

  def uniqueEmail(email: String) =
    db.run(table.filter(_.email === email).result).map(_.isEmpty)

  def createNewUser(userCreate: UserCreate) = {
    val usernameIsUnique = uniqueUsername(userCreate.username)
    val emailIsUnique = uniqueEmail(userCreate.email)

    Future.sequence(Seq(usernameIsUnique, emailIsUnique)).flatMap(k => (k.head, k(1)) match {
      case (true, true) =>
        create(userCreate.makeUser)
      case _ =>
        throw new Exception("Must provide unique email and username")
    })
  }

  def updateUser(updateUser: UpdateUser) = {
    val user = authenticatedUser(updateUser.userLogin)
    if(user.isDefined){
      val newUser = (updateUser.newEmail, updateUser.newPassword) match {
        case (Some(ne), Some(np)) => user.get.copy(email = ne, hashedPassword = makeHash(np))
        case (Some(ne), None) => user.get.copy(email = ne)
        case (None, Some(np)) => user.get.copy(hashedPassword = makeHash(np))
        case _ => user.get
      }
      save(newUser)
    } else {
      throw new Exception("Unable to authenticate")
    }
  }
}