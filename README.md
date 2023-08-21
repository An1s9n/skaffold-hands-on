# Skaffold hands on

This is a sample joke-service showing how [Skaffold](https://skaffold.dev) can ease development

### Prerequisites

- JDK 17
- [Minikube](https://minikube.sigs.k8s.io/docs/start)
- [Skaffold](https://skaffold.dev/docs/install)

### Service short description

Joke-service is an
ordinary [Kotlin](https://kotlinlang.org) + [Spring Boot](https://spring.io/projects/spring-boot) + [Maven](https://maven.apache.org)
web service exposing single endpoint for fetching random jokes. Service can fetch jokes from 2 different sources
(see [joke-api.http](joke-api.http)):

- internal [Postgresql](https://www.postgresql.org) database
- external [Chuck Norris](https://api.chucknorris.io) api

Each valid request is also logged to database

### Project structure

- [src](src): contains, non surprisingly, joke-service and tests source code
- [k8s](k8s): contains k8s manifests: [base](k8s/base) folder is for the joke-serivce itself
  and [dev/deps](k8s/dev/deps) folder is for dev environment ([postgresql.yaml](k8s/dev/deps/postgres/postgres.yaml) for
  Postgresql database and [wiremock.yaml](k8s/dev/deps/wiremock/wiremock.yaml) as an external
  api [Wiremock](https://wiremock.org))
- [skaffold.yaml](skaffold.yaml): Skaffold config file

### How to use Skaffold

> **_NOTE:_** Throughout this section [skaffold.yaml](skaffold.yaml) config file will be referenced as simply "config"

Start your Minikube before running Skaffold commands, for example, like this:

```sh
minikube start --memory=10g --cpus=6 --disk-size='120000mb' --addons=metrics-server
```

#### dev mode

All you need to do to build an image for joke-service and deploy it as well as dev environment to Minikube in dev mode
is to run:

```sh 
skaffold dev --port-forward
```

This command will do following:

- build an image for joke-service according to `build` config section
- deploy k8s resources referenced in `manifests.kustomize.paths` config section to Minikube (joke-service itself and dev
  environment)
- forward k8s resources' ports to your localhost thanks to `--port-forward` option
- continuously rebuild and redeploy resources if some changes are encountered in k8s manifests or in files referenced
  in `build.artifacts.custom.dependencies.paths` config section

Now joke-service is up and running, you can access it via localhost, play with it, try to change some files and see what
happens

#### dev environment only mode

If you want to deploy to Minikube only dev environment without building and deploying the joke-service then run:

```sh 
skaffold dev --port-forward -p depsOnly
```

This command will apply `depsOnly` profile from config, see `profiles` section. Differences with previous command:

- Skaffold will not build image for joke-service
- only k8s manifests describing dev environment resources will be picked up

Now you can launch joke-service as you wish, for example with magic green `run` or `debug` button in IntelliJ, just
don't forget to activate `dev` profile

#### debug mode

In order to debug run:

```sh 
skaffold debug --port-forward
```

What this command will do differently compared to `dev` command:

- expose additional debug port (5005 by default), which will be also forwarded to your localhost
- Skaffold will not be tracking changes in k8s manifests and referenced files

Now you can attach your IDE to the debug port, and it will start hitting breakpoints! In order to do so in IntelliJ
simply add and launch default `Remote JVM Debug` configuration
