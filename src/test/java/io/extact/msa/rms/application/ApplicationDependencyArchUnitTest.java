package io.extact.msa.rms.application;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.rms", importOptions = ImportOption.DoNotIncludeTests.class)
class ApplicationDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // applicationパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * webapiパッケージ内のアプリのコードで依存OKなライブラリの定義。
     */
    @ArchTest
    static final ArchRule test_webapiパッケージで依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.application.webapi..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.application..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.jwt..",
                                "org.eclipse.microprofile.openapi..",
                                "org.eclipse.microprofile.rest.client..",
                                "javax.annotation..",
                                "javax.inject..",
                                "javax.enterprise.context..",
                                "javax.enterprise.inject..",
                                "javax.validation..",
                                "javax.ws.rs..",
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
                    .resideInAPackage("io.extact.msa.rms.application.service..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.application..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "javax.inject..",
                                "javax.enterprise.context..",
                                "javax.enterprise.event..",
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
                    .resideInAPackage("io.extact.msa.rms.application.external..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.application..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.rest.client..",
                                "javax.inject..",
                                "javax.enterprise.context..",
                                "javax.ws..",
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
                    .resideInAPackage("io.extact.msa.rms.application.model..")
                .should()
                    .onlyDependOnClassesThat()
                        .resideInAnyPackage(
                                "io.extact.msa.rms.platform.fw.domain..",
                                "io.extact.msa.rms.application.model..",
                                "org.apache.commons.lang3..",
                                "javax.validation..",
                                "java.."
                                );
}
