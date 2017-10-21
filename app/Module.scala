import com.google.inject.AbstractModule
import java.time.Clock

import com.twilio.Twilio

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    //Ensure required properties are set
    if (sys.env("TWILIO_USERNAME") == null) {
      throw new IllegalArgumentException("TWILIO_USERNAME is required")
    }
    if (sys.env("TWILIO_PASSWORD") == null) {
      throw new IllegalArgumentException("TWILIO_PASSWORD is required")
    }
    if (sys.env("TWILIO_PHONE") == null) {
      throw new IllegalArgumentException("TWILIO_PHONE is required")
    }
    Twilio.init(sys.env("TWILIO_USERNAME"), sys.env("TWILIO_PASSWORD"));
  }

}
