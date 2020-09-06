# Running locally

## Run mongo on docker

`docker run -p 27017:27017 --name career-app-mongo -d mongo`

## Run RabbitMQ on Docker

`docker run -d --name career-rabbit -p 5672:5672 -p 5673:5673 -p 15672:15672 rabbitmq:3-management`

[http://localhost:15672/](http://localhost:15672/)

#### Credentials

* guest
* guest 

## Run Tests Locally

To run integrated tests locally, need to inform the environment variables to maven, get it from credentials to run in Mongo INT Tests

`mvn clean package -DMONGO_PASSWORD=<password> -DMONGO_USER=<user> -DMONGO_HOST=<host>`

## Artifact Version
`mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q`

## Build Package

`mvn clean package`

## Build Image 

`docker build -t allanweber/candidates-career:<version> .`  
[Version in Artifact Version](#artifact-version)

## Tag Latest if Master

`docker tag allanweber/candidates-career:<version> allanweber/candidates-career:latest`

## Docker Run

### Local

`docker run -p 8080:8080 --name candidates-career -e SPRING_PROFILES_ACTIVE=local allanweber/candidates-career:latest`

### DEV

`docker run -p 8080:8080 --name candidates-career -e SPRING_PROFILES_ACTIVE=dev -e MONGO_USER=<user> -e MONGO_PASS=<pass> -e MONGO_HOST=<host> allanweber/candidates-career:latest`

### PRD

`docker run -p 8080:8080 --name candidates-career -e MONGO_USER=<user> -e MONGO_PASS=<pass> -e MONGO_HOST=<host> allanweber/candidates-career:latest`