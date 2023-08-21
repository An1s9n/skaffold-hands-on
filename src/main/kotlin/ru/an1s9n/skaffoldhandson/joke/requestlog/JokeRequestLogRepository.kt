package ru.an1s9n.skaffoldhandson.joke.requestlog

import ru.an1s9n.skaffoldhandson.joke.Joke
import ru.an1s9n.skaffoldhandson.joke.JokeSource

interface JokeRequestLogRepository {

  fun saveNewReturningId(source: JokeSource): Long

  fun markOk(id: Long, joke: Joke)

  fun markNotFound(id: Long)

  fun markError(id: Long, errorMessage: String)
}
