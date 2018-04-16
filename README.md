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

There are three ways that the service can be run locally:

| Service Environment | Database Environment |
| ------------------- | -------------------- |
| Host | Host
| Host | Docker
| Docker | Docker

Using setup 3 is the recommended setup for getting started. This is because Docker creates a clean environment so we
can ensure that if any issues occur in setup 1 or 2, it's highly likely that its a local environment issue.

For the most rapid development, we recommend using either setup 1 or 2. Setup 2 is optimal in our opinion because it's
really easy to create a new clean DB environment and ensure that the DB environment cannot be influenced by any other DB
clients you may have running locally.

### Service on Host, DB on Host

Start the service using the following command. It will attempt to connect to your locally running SQL database on the
standard port, 3306.

```bash
cd ScalaSmsAlertSystem
# username = your Account SID
export TWILIO_USERNAME="..."
# password = auth token
export TWILIO_PASSWORD="..."
# the phone number you created
export TWILIO_PHONE="..."
sbt clean compile stage
./target/universal/stage/bin/smsalertsystemv2 \
  -Dplay.evolutions.db.default.autoApply=true \
	-Dplay.http.secret.key=l33t_52uc3 \
	-Dslick.dbs.default.db.user=$MYSQL_USER \
	-Dslick.dbs.default.db.password=$MYSQL_PASSWORD
```

If the database is listening on a non-standard port, then specify the `slick.dbs.default.db.url` property, which will
have the form:

```
-Dslick.dbs.default.db.url=jdbc:mysql://0.0.0.0:${PORT_NUMBER}/sms
```

You can find additional properties in the `./conf/application.conf` file.  You should now be able to go to your browser
and go to the URL `http://localhost:${PORT_NUMBER}` to use the service.

### Service on Host, DB on Docker

First install [Docker][] in which we will create a Docker container running [MariaDB][]. The [MariaDB Docker Image][] is
hosted on Docker Hub. To start the container run:

```bash
docker run -e MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD -e MYSQL_DATABASE=sms -p 3306:3306 -d mariadb:latest
```

This will pull down the latest `mariadb` image and bind port 3306 on the host to 3306 on the container. If you have a
locally running database already, then you will need to use a different port. 

This command will start a Docker container using the `latest` version of `mariadb` configured to use the password
specified by `MYSQL_ROOT_PASSWORD`. The `-p` option is used to map the port 3306 on the local host to the `3306` port in
the Docker container, which is the port that `mariadb` will be listening on. If you want to have the database listening
on a different host port, you an option such as `-p 6033:3306`. This will be needed if you have a locally running
database that has already bound to that port. You will also need to configure the service to use the non-standard port,
which was mentioned in the previous section.

You can test if the Docker container is working by using a `mysql` client that you have installed locally:
```bash
mysql -h 0.0.0.0 -p -u root
```

Description of the options are:
* `-h`: specifies the host
* `-p`: specifies that a password is required; you will be prompted and should enter whatever value was used for 
        `MYSQL_ROOT_PASSWORD`
* `-u`: the user to connect as

You can then start the service the same as the previous section using the properties:

```
-Dslick.dbs.default.db.user=root \
-Dslick.dbs.default.db.password=$MYSQL_ROOT_PASSWORD
```

By default, the service is configured to use the password `example`, but this is obviously not used in production and is
just a default value.

[Docker]: https://www.docker.com/community-edition#/download
[MariaDB]: https://mariadb.org/
[MariaDB Docker Image]: https://hub.docker.com/_/mariadb/

### Service on Docker, DB on Docker

We will use Docker Compose to run the service and DB in a Docker container. Please see the 
[Docker Compose Installation page][] for installation instructions. Docker compose will run the instructions specified
in the `docker-compose.yml` file. Running and tearing down is very easy:

```bash
docker-compose up -d
docker-compose down
```

This will start both the DB and the service in a docker container and set up the necessary links between them.

[Docker Compose Installation page]: https://docs.docker.com/compose/install/

# Deploying to the Cloud

The cloud formation stack is found in the repository:
* [sms-alert-system-cloud-formation][]

[sms-alert-system-cloud-formation]: https://github.com/ColectivaLegal/sms-alert-system-cloud-formation
