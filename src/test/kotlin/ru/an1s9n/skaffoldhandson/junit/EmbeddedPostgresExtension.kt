package ru.an1s9n.skaffoldhandson.junit

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.IOException
import java.sql.Connection
import java.sql.SQLException

class EmbeddedPostgresExtension : BeforeAllCallback, AfterAllCallback {

  @Volatile
  private var embeddedPostgres: EmbeddedPostgres? = null

  @Volatile
  private var postgresConnection: Connection? = null

  override fun beforeAll(context: ExtensionContext?) {
    embeddedPostgres = EmbeddedPostgres.builder().start()
    postgresConnection = embeddedPostgres?.postgresDatabase?.connection
    System.setProperty("embedded-postgres.host", "localhost")
    System.setProperty("embedded-postgres.port", "${embeddedPostgres!!.port}")
    System.setProperty("embedded-postgres.db", "postgres")
    System.setProperty("embedded-postgres.user", "postgres")
    System.setProperty("embedded-postgres.password", "postgres")
  }

  override fun afterAll(context: ExtensionContext?) {
    try {
      postgresConnection!!.close()
    } catch (sqlE: SQLException) {
      throw AssertionError(sqlE)
    }
    try {
      embeddedPostgres!!.close()
    } catch (ioE: IOException) {
      throw AssertionError(ioE)
    }
  }
}
