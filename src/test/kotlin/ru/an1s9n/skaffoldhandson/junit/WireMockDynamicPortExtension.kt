package ru.an1s9n.skaffoldhandson.junit

import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo

class WireMockDynamicPortExtension : WireMockExtension(extensionOptions().options(options().dynamicPort())) {

  override fun onBeforeAll(wireMockRuntimeInfo: WireMockRuntimeInfo) {
    System.setProperty("wiremock.base-url", wireMockRuntimeInfo.httpBaseUrl)
  }
}
