# Sms Alert System for Scala
[![Build Status](https://travis-ci.org/ColectivaLegal/ScalaSmsAlertSystem.svg?branch=master)](https://travis-ci.org/ColectivaLegal/ScalaSmsAlertSystem)

Below you will find basic setup instructions for developing the SysmAlertSystem. 

To begin with, please ensure you have the following installed on your local development system:
* JDK 8
* [SBT](http://www.scala-sbt.org/download.html)

JDK8 is currently required due to some incompatibilities between SBT's ivy implementation and JDK9 

## Getting Started: Running Locally

Before you can launch the application locally, you need a Twilio account to send and receive messages. You will need to set environment variables which define the account security identifier, authorization token, associated phone number. After those have been defined, you can invoke sbt to start it:

```
cd ScalaSmsAlertSystem
export TWILIO_USERNAME="..."
export TWILIO_PASSWORD="..."
export TWILIO_PHONE="..."
sbt run
```

This will launch a local version of the application with in memory database. The first time you load the page you will be asked to update the database schema but then it should be functional.

# Deploying to the Cloud

Coming soon!