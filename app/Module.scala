import ServerLifecycleImpl.ServerLifecycle
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.sns.{AmazonSNS, AmazonSNSClientBuilder}
import com.google.inject.{AbstractModule, Provides}
import play.api.Configuration

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
    bind(classOf[ServerLifecycle]).to(classOf[ServerLifecycleImpl]).asEagerSingleton()
  }

  @Provides
  def amazonSnsClient(config: Configuration): AmazonSNS = {
    AmazonSNSClientBuilder
      .standard()
      .withCredentials(new DefaultAWSCredentialsProviderChain())
      .withRegion(config.underlying.getString(Module.AwsRegionConfigKey))
      .build()
  }
}

private object Module {
  val AwsRegionConfigKey: String = "aws.region"
}
