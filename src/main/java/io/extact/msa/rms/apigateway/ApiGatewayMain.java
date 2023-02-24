package io.extact.msa.rms.apigateway;

import io.extact.msa.rms.platform.fw.webapi.BootstrapWebApi;

public class ApiGatewayMain {

    private static final String ENV_VAL_TLS_FILE_PATH = "ENV_RMS_SEC_TLS_FILE";
    private static final String CONFIG_KEY_TLS_FILE_PATH = "env.rms.sec.tls.file";
    private static final String CONFIG_KEY_TLS_RESOURCE_PATH1 = "server.sockets.0.tls.private-key.keystore.resource.path";
    private static final String CONFIG_KEY_TLS_RESOURCE_PATH2 = "server.sockets.0.tls.trust.keystore.resource.path";
    private static final String CONFIG_VALUE_TLS_RESOURCE_PATH = "${" + CONFIG_KEY_TLS_FILE_PATH + "}";

    public static void main(String[] args) throws Exception {
        prepareSystemProperties();
        BootstrapWebApi.start(args);
    }

    // ---
    // ハイフン(-)を含むキーは環境変数で設定できない
    // ${env.rms.sec.tls.file}をnullでバインドできない
    // なのでプログラムで環境変数をもとにシステムプロパティを設定している
    private static void prepareSystemProperties() {
        var envValue = System.getenv(ENV_VAL_TLS_FILE_PATH);
        if (envValue == null || envValue.isBlank()) {
            return;
        }
        var propValue = System.getProperty(CONFIG_KEY_TLS_RESOURCE_PATH1);
        if (propValue != null) {
            return;
        }
        System.setProperty(CONFIG_KEY_TLS_FILE_PATH, envValue);
        System.setProperty(CONFIG_KEY_TLS_RESOURCE_PATH1, CONFIG_VALUE_TLS_RESOURCE_PATH);
        System.setProperty(CONFIG_KEY_TLS_RESOURCE_PATH2, CONFIG_VALUE_TLS_RESOURCE_PATH);
    }
}
