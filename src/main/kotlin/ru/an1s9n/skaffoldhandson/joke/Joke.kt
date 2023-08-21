package ru.an1s9n.skaffoldhandson.joke

data class Joke(val id: String, val text: String)

sealed interface FindJokeResult {

  data class Ok(val joke: Joke) : FindJokeResult

  object NotFound : FindJokeResult

  data class Error(val message: String) : FindJokeResult
}
