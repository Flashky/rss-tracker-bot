# rss-tracker-bot
[![Build & Report](https://github.com/Flashky/rss-tracker-bot/actions/workflows/build-report.yml/badge.svg)](https://github.com/Flashky/rss-tracker-bot/actions/workflows/build-report.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/764da114801347c2a2de95c572ec108a)](https://www.codacy.com/gh/Flashky/rss-tracker-bot/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Flashky/rss-tracker-bot&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/764da114801347c2a2de95c572ec108a)](https://www.codacy.com/gh/Flashky/rss-tracker-bot/dashboard?utm_source=github.com&utm_medium=referral&utm_content=Flashky/rss-tracker-bot&utm_campaign=Badge_Coverage)

## Configuration properties

Before running the service, you will need to customize several properties or environment variables:

Property | Description | Default value
--|--|--
``TELEGRAM_BOT_TOKEN`` | The Telegram Bot token provided by [@BotFather](https://t.me/botfather) | None
``MONGODB_HOST`` | The MongoDB instance hostname or ip | ``localhost``
``MONGODB_PORT`` | The MongoDB port | ``27017``
``MONGODB_USERNAME`` | The MongoDB username | ``admin`` **(*)**
``MONGODB_PASSWORD`` | The MongoDB password | ``admin`` **(*)**
``MONGODB_DATABASE`` | The MongoDB database | ``rss_tracker_db``

***:** *It is highly recommended to change both default username and password.*

Please note:
- For [Docker](#docker-standalone) and [Docker Compose](#docker-compose) running modes, you might want to use a ``.env`` file to configure the previous properties.
- The property ``MONGODB_HOST`` value is ignored when using [Docker Compose](#docker-compose) running mode. It will use a default hostname ``mongodb`` specified by the ``docker-compose.yml`` file. 


## Running the service

There are several ways to run the service.

- Java Standalone
- Docker Standalone
- Docker Compose

### Java Standalone

#### Requirements

- JDK 11.
- Maven.
- MongoDB instance running with user and password.

#### Build

Open a terminal, clone the repo and build with maven:

```ssh
git clone https://github.com/Flashky/rss-tracker-bot.git
cd rss-tracker-bot
mvn clean package
```
#### Configure environment variables

Edit ``run_standalone.sh`` to modify the [configuration properties](#configuration-properties) as you need.

#### Run

Give permissions and execute:

```ssh
chmod +x run_standalone.sh
./run_standalone.sh
```

### Docker Standalone

#### Requirements

- Docker.
- MongoDB instance running with user and password.

#### Image pull

Download the latest docker image:

```shell
docker pull flashk/rss-tracker-bot:latest
```

#### Run

Basic run, asumming you have declared the needed [configuration properties](#configuration-properties) as environment variables:

```shell
docker run --name rss-tracker-bot -dp 8080:8080 flashk/rss-tracker-bot:latest
```

In case of using a ``.env`` file to configure the properties, you can run the container using the ``--env-file`` flag:

```shell
docker run --name rss-tracker-bot --env-file .env -dp 8080:8080 flashk/rss-tracker-bot:latest
```

### Docker Compose

#### Requirements

- Docker.

#### Run

Command:

```shell
docker compose up
```

The previous command will setup a Docker container with MongoDB and another Docker container with the service. 
It will use ``.env`` file by default for getting the [configuration properties](#configuration-properties).

If you want to use multiple environments you can duplicate ``.env``, customize its values, and finally run ``docker compose`` specifying ``--env-file`` flag  to select which configuration to use.

Example with a ``.env.dev`` file:

```shell
docker compose --env-file .env.dev up 
```