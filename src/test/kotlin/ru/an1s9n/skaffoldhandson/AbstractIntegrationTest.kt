package ru.an1s9n.skaffoldhandson

import com.github.database.rider.junit5.DBUnitExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalManagementPort
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import ru.an1s9n.skaffoldhandson.junit.EmbeddedPostgresExtension

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@TestConstructor(autowireMode = ALL)
@ExtendWith(EmbeddedPostgresExtension::class, DBUnitExtension::class)
@DirtiesContext
abstract class AbstractIntegrationTest {

  @LocalServerPort
  protected final var localServerPort: Int = 0
    private set

  @LocalManagementPort
  protected final var localManagementPort: Int = 0
    private set
}
