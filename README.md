# Sms Alert System for Scala
[![Build Status](https://travis-ci.org/ColectivaLegal/ScalaSmsAlertSystem.svg?branch=master)](https://travis-ci.org/ColectivaLegal/ScalaSmsAlertSystem)

Below you will find basic setup instructions for developing the SysmAlertSystem. 

To begin with, please ensure you have the following installed on your local development system:
* JDK 8
* [SBT](http://www.scala-sbt.org/download.html)

JDK8 is currently required due to some incompatibilities between SBT's ivy implementation and JDK9 

## Twilio Setup

You'll need a Twilio account to send and receive messages. You can
sign up for a trial account for free.

After signing up, click on "Get Started" and follow the screens to
create a phone number. This is the number that will be used for
subscribing to the alert system.

You'll need to configure Twilio to communicate with the webhook
endpoint in this Scala application. Go to "Manage Numbers" and click
the number you created. On the configuration screen, scroll to the
"Messaging" section and find the label "A Message Comes In". Make sure
"Webhook" is selected and enter the web-accessible URL of your
application with the following path:

http://your.domain/twilio/message

If you are running on a local development instance behind a
NAT/firewall, you can use [ngrok](https://ngrok.com/) to create a
tunnel from a web-accessible address to your machine.

## Getting Started: Running Locally

You will need to set environment variables with your Twilio account
settings. After those have been defined, you can invoke sbt to start
it:

```
cd ScalaSmsAlertSystem
# username = your Account SID
export TWILIO_USERNAME="..."
# password = auth token
export TWILIO_PASSWORD="..."
# the phone number you created
export TWILIO_PHONE="..."
sbt run
```

This will launch a local version of the application with in memory database. The first time you load the page you will be asked to update the database schema but then it should be functional.

# Deploying to the Cloud

Coming soon!
