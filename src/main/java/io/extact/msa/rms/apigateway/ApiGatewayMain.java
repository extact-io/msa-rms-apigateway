package io.extact.msa.rms.apigateway;

import io.extact.msa.rms.platform.fw.webapi.server.BootstrapWebApi;

public class ApiGatewayMain {
    public static void main(String[] args) throws Exception {
        BootstrapWebApi.start(args);
    }
}