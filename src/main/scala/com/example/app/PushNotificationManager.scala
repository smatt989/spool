package com.example.app

import java.io.File
import java.util.concurrent.ExecutionException

import com.relayrides.pushy.apns._
import com.relayrides.pushy.apns.util.{ApnsPayloadBuilder, SimpleApnsPushNotification, TokenUtil}
import java.util.concurrent.{Future => JFuture}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise, Future => SFuture}
import scala.util.Try

object PushNotificationManager {

  val apnsClient = new ApnsClientBuilder().build()
  val topic = "com.spool.app"
  val teamId = System.getenv("SPOOL_TEAM_ID")
  val keyId = System.getenv("APNS_KEY_ID")

  val file = new File("src/main/resources/APNsAuthKey_VLPZR3288Q.p8")

  apnsClient.registerSigningKey(new File("src/main/resources/APNsAuthKey_VLPZR3288Q.p8"),
    teamId, keyId, topic)

  val developerGateway = "gateway.sandbox.push.apple.com"
  val productionGateway = "gateway.push.apple.com"

  def makePushNotification(message: String, deviceToken: String) = {

    val jfuture: JFuture[Void] = apnsClient.connect(developerGateway)
    val promise = Promise[Void]()
    new Thread(new Runnable { def run() { promise.complete(Try{ jfuture.get }) }}).start
    val future = promise.future

    System.out.println("Establishing connection...");

    Await.result(future, Duration.Inf)

    val payloadBuilder = new ApnsPayloadBuilder()
    payloadBuilder.setAlertBody(message)

    val payload = payloadBuilder.buildWithDefaultMaximumLength()

    val token = TokenUtil.sanitizeTokenString(deviceToken)

    val pushNotification = new SimpleApnsPushNotification(token, topic, payload)
    System.out.println("Sending notification...");
    val sendNotificationFuture = apnsClient.sendNotification(pushNotification)

    try {
      val pushNotificationResponse =
        sendNotificationFuture.get();

      System.out.println("Push notification received...");
      if (pushNotificationResponse.isAccepted()) {
        System.out.println("Push notification accepted by APNs gateway.");
      } else {
        System.out.println("Notification rejected by the APNs gateway: " +
          pushNotificationResponse.getRejectionReason());

        if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
          System.out.println("\t…and the token is invalid as of " +
            pushNotificationResponse.getTokenInvalidationTimestamp());
        }
      }
    } catch {case e: ExecutionException =>
      System.err.println("Failed to send push notification.");
      e.printStackTrace();

      if (e.getCause().isInstanceOf[ClientNotConnectedException]) {
        System.out.println("Waiting for client to reconnect…");
        apnsClient.getReconnectionFuture().await();
        System.out.println("Reconnected.");
      }
    }

    val jfutureDisconnect: JFuture[Void] = apnsClient.disconnect()
    val promiseDisconnect = Promise[Void]()
    new Thread(new Runnable { def run() { promiseDisconnect.complete(Try{ jfutureDisconnect.get }) }}).start
    val futureDisconnect = promiseDisconnect.future

    Await.result(futureDisconnect, Duration.Inf)
  }
}
