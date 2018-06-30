import com.twilio.Twilio
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Logger}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future
import ServerLifecycleImpl._

@Singleton
class ServerLifecycleImpl @Inject()(config: Configuration, appLifecycle: ApplicationLifecycle) extends ServerLifecycle {

  def onStart(): Unit = {
    //Ensure required properties are set
    Array(TwilioUserNameConfigKey, TwilioPasswordConfigKey, TwilioPhoneConfigKey).foreach(key => {
      val configVal: String = config.underlying.getString(key)
      Logger.info(s"Config: (key,value) = ($key,$configVal")
      if (configVal == null) {
        throw new IllegalArgumentException(s"$key is required")
      }
    })

    Twilio.init(config.underlying.getString(TwilioUserNameConfigKey), config.underlying.getString(TwilioPasswordConfigKey))
  }

  val onStop: () => Future[Unit] = () => Future.successful(())

  appLifecycle.addStopHook(onStop)
  onStart()
}

object ServerLifecycleImpl {
  val TwilioUserNameConfigKey: String = "twilio.username"
  val TwilioPasswordConfigKey: String = "twilio.password"
  val TwilioPhoneConfigKey: String = "twilio.phone"

  trait ServerLifecycle {
    def onStart(): Unit
  }
}
