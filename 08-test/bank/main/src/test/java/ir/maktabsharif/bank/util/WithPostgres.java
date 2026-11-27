package ir.maktabsharif.bank.util;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import ir.maktabsharif.bank.TestApplicationContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class WithPostgres {
    protected static TestApplicationContext context;
    protected static PostgreSQLContainer postgres;

    @BeforeAll
    static void beforeAll() {
        context = TestApplicationContext.getInstance("test-config.properties");
        var properties = context.getProperties();
        var port = Integer.parseInt(properties.get("testcontainers.postgres.port"));
        postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
                .withUsername(properties.get("jpa.jakarta.persistence.jdbc.user"))
                .withPassword(properties.get("jpa.jakarta.persistence.jdbc.password"))
                .withDatabaseName(properties.get("testcontainers.postgres.database"))
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(
                                new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(5432)))
                ));
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        if (postgres != null) {
            postgres.stop();
        }
    }
}
