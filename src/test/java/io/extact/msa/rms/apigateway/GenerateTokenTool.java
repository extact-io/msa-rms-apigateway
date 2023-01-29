package io.extact.msa.rms.apigateway;

import java.util.Set;

import jakarta.enterprise.inject.spi.CDI;

import io.extact.msa.rms.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;
import io.extact.msa.rms.platform.fw.webapi.BootstrapWebApi;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class GenerateTokenTool {
    public static void main(String[] args) throws Exception {
        System.setProperty("server.port", "-1");
        BootstrapWebApi.start(args);

        var testUser = new TempUserClaims("1234", "1234@extact.io", Set.of("ADMIN", "MEMBER"));
        var generator = CDI.current().select(JsonWebTokenGenerator.class).get();
        var idToken = generator.generateToken(testUser);

        System.out.println("------------");
        System.out.println(idToken);
        System.out.println("------------");

        System.exit(0);
    }
    @AllArgsConstructor
    @Getter
    static class TempUserClaims implements UserClaims {
        private String userId;
        private String userPrincipalName;
        private Set<String> groups;
    }
}
