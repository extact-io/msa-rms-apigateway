package io.extact.msa.rms.apigateway;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.rms", importOptions = ImportOption.DoNotIncludeTests.class)
class ApiGatewayDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // apigatewayパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * webapiパッケージ内のアプリのコードで依存OKなライブラリの定義。
     */
    @ArchTest
    static final ArchRule test_webapiパッケージで依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.apigateway.webapi..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.apigateway..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.jwt..",
                                "org.eclipse.microprofile.openapi..",
                                "org.eclipse.microprofile.rest.client..",
                                "jakarta.annotation..",
                                "jakarta.inject..",
                                "jakarta.enterprise.context..",
                                "jakarta.enterprise.inject..",
                                "jakarta.validation..",
                                "jakarta.ws.rs..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

    /**
     * serviceパッケージ内のアプリのコードで依存OKなライブラリの定義。依存してよいのは以下のモノのみ
     */
    @ArchTest
    static final ArchRule test_serviceパッケージで依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.apigateway.service..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.apigateway..",
                                "io.opentracing..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "jakarta.inject..",
                                "jakarta.enterprise.context..",
                                "jakarta.enterprise.event..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

    /**
     * externalパッケージ内のアプリのコードで依存OKなライブラリの定義。
     */
    @ArchTest
    static final ArchRule test_externalパッケージで依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.apigateway.external..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.apigateway..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.rest.client..",
                                "jakarta.inject..",
                                "jakarta.enterprise.context..",
                                "jakarta.ws..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

    /**
     * modelパッケージ内部の依存関係の定義
     */
    @ArchTest
    static final ArchRule test_modelパッケージのクラスが依存してよいパッケージの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.apigateway.model..")
                .should()
                    .onlyDependOnClassesThat()
                        .resideInAnyPackage(
                                "io.extact.msa.rms.platform.fw.domain..",
                                "io.extact.msa.rms.apigateway.model..",
                                "org.apache.commons.lang3..",
                                "jakarta.validation..",
                                "java.."
                                );
}
