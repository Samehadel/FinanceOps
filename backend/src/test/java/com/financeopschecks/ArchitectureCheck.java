package com.financeopschecks;

import com.financeops.FinanceOpsApplication;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.springframework.modulith.core.ApplicationModules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/** Build-time static check, without starting Spring, a database, or a behavioral test suite. */
public final class ArchitectureCheck {
    private ArchitectureCheck() {
    }

    public static void main(String[] args) {
        ApplicationModules modules = ApplicationModules.of(FinanceOpsApplication.class).verify();
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.financeops");

        noClasses().that().resideInAnyPackage("..domain..", "..contract..", "..spi..")
            .and().doNotHaveSimpleName("package-info")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..", "jakarta.persistence..", "jakarta.servlet..",
                "com.financeops..api..", "com.financeops..application..",
                "com.financeops..persistence..", "com.financeops..security..")
            .because("domain rules and module contracts are independent of frameworks and adapters")
            .check(classes);

        noClasses().that().resideInAnyPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.financeops..api..", "com.financeops..persistence..",
                "com.financeops..security..", "jakarta.persistence..",
                "jakarta.servlet..", "org.springframework.web..", "org.springframework.data..",
                "org.springframework.jdbc..", "org.springframework.security.web..",
                "org.springframework.security.core..")
            .because("application services depend on ports and trusted identity contracts")
            .check(classes);

        noClasses().that().resideInAnyPackage("..api..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.financeops..persistence..", "jakarta.persistence..", "org.springframework.data..",
                "org.springframework.jdbc..", "java.sql..")
            .because("HTTP controllers must not query the database directly")
            .check(classes);

        modules.forEach(module -> System.out.println("Verified module: " + module.getIdentifier()));
        System.out.println("Module boundaries and layer dependencies verified.");
    }
}
