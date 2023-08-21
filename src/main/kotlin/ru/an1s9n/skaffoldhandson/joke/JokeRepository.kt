package ru.an1s9n.skaffoldhandson.joke

interface JokeRepository {

  fun supportedSource(): JokeSource

  fun findRandomJoke(): FindJokeResult
}
