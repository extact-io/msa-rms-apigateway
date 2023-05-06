package io.extact.msa.rms.apigateway.webapi;

import static java.nio.file.StandardOpenOption.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.extact.msa.rms.apigateway.external.stub.RentalItemApiRemoteStub;
import io.extact.msa.rms.apigateway.external.stub.ReservationApiRemoteStub;
import io.extact.msa.rms.apigateway.external.stub.ServiceApiRemoteStubApplication;
import io.extact.msa.rms.apigateway.external.stub.UserAccountApiRemoteStub;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.extact.msa.rms.test.utils.ClearOpenTelemetryContextCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddExtension(ClearOpenTelemetryContextCdiExtension.class)
@AddBean(RentalItemApiRemoteStub.class)
@AddBean(ReservationApiRemoteStub.class)
@AddBean(UserAccountApiRemoteStub.class)
@AddBean(ServiceApiRemoteStubApplication.class)
@AddConfig(key = "server.port", value = "7001") // for REST server
@ExtendWith(JulToSLF4DelegateExtension.class)
//@EnabledIfSystemProperty(named = "mvn.gen-openapi.profile", matches = "on")
class GenerateOasFileTest {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateOasFileTest.class);
    private Client client;

    @BeforeEach
    void setup() {
        this.client = ClientBuilder.newClient();
    }

    @Test
    void generateOasFile() throws Exception {
        WebTarget target = client.target("http://localhost:7001").path("/openapi");
        String body = target
                .request()
                .header("Accept", "text/yaml") // Acceptを設定しないとOpenAPI UIのHTMLが返される
                .get(String.class);
        Path filePath = Files.createDirectories(Paths.get("./target/generated-oas")).resolve("openapi.yml");
        Files.writeString(filePath, body, CREATE, WRITE, TRUNCATE_EXISTING); // 上書きモード
        LOG.info("OASファイルを生成しました [Path:{}]", filePath.toAbsolutePath());
    }
}
