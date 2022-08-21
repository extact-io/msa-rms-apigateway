package io.extact.msa.rms.application;

import static com.tngtech.archunit.library.Architectures.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.rms.application", importOptions = ImportOption.DoNotIncludeTests.class)
class ApplicationLayerDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // rms.applicationのアプリケーションアーキテクチャレベルの依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * アプリケーションアーキテクチャのレイヤと依存関係の定義
     * <pre>
     * ・webapiレイヤはどのレイヤからも依存されていないこと（webapiレイヤは誰も使ってはダメ））
     * ・serviceレイヤaはwebapiレイヤからのみ依存を許可（serviceレイヤを使って良いのはwebapiレイヤのみ）
     * ・externalレイヤはserviceレイヤからのみ依存を許可（externalレイヤを使って良いのはserviceレイヤのみ）
     * ・modelレイヤは上記3つのレイヤからの依存を許可
     * </pre>
     */
    @ArchTest
    static final ArchRule test_レイヤー間の依存関係の定義 = layeredArchitecture()
            .layer("webapi").definedBy("io.extact.msa.rms.application.webapi..")
            .layer("service").definedBy("io.extact.msa.rms.application.service..")
            .layer("external").definedBy("io.extact.msa.rms.application.external..")
            .layer("model").definedBy("io.extact.msa.rms.application.model..")

            .whereLayer("webapi").mayNotBeAccessedByAnyLayer()
            .whereLayer("service").mayOnlyBeAccessedByLayers("webapi")
            .whereLayer("external").mayOnlyBeAccessedByLayers("service")
            .whereLayer("model").mayOnlyBeAccessedByLayers("webapi", "service", "external");
}
