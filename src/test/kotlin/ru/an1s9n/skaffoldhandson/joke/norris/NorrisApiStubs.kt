package ru.an1s9n.skaffoldhandson.joke.norris

import com.github.tomakehurst.wiremock.client.WireMock
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.get
import com.marcinziolo.kotlin.wiremock.returns
import com.marcinziolo.kotlin.wiremock.returnsJson

fun WireMock.stubOkValidResponse() {
  get { urlPath equalTo "/jokes/random" } returnsJson {
    body = /*language=JSON*/ """
      {
        "categories": ["religion"],
        "created_at": "2020-01-05 13:42:19.576875",
        "icon_url": "https://assets.chucknorris.host/img/avatar/chuck-norris.png",
        "id": "zuf836jurwgszxegfxe76g",
        "updated_at": "2020-01-05 13:42:19.576875",
        "url": "https://api.chucknorris.io/jokes/zuf836jurwgszxegfxe76g",
        "value": "The Bible was originally titled \"Chuck Norris and Friends\""
      }
    """
  }
}

fun WireMock.stubOkEmptyResponse() {
  get { urlPath equalTo "/jokes/random" } returnsJson {}
}

fun WireMock.stubOkMalformedResponse() {
  get { urlPath equalTo "/jokes/random" } returnsJson {
    body = /*language=JSON*/ """
      {
        "categories": ["religion"],
        "created_at": "2020-01-05 13:42:19.576875",
        "icon_url": "https://assets.chucknorris.host/img/avatar/chuck-norris.png",
        "id": "zuf836jurwgszxegfxe76g",
        "updated_at": "2020-01-05 13:42:19.576875",
        "url": "https://api.chucknorris.io/jokes/zuf836jurwgszxegfxe76g",
        "malformed_field_name": "The Bible was originally titled \"Chuck Norris and Friends\""
      }
    """
  }
}

fun WireMock.stubNotFound() {
  get { urlPath equalTo "/jokes/random" } returns { statusCode = 404 }
}

fun WireMock.stubServiceUnavailable() {
  get { urlPath equalTo "/jokes/random" } returns { statusCode = 503 }
}
