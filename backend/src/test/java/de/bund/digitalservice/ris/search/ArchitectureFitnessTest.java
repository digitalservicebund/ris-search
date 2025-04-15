package de.bund.digitalservice.ris.search;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.Predefined;
import com.tngtech.archunit.library.dependencies.SliceRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ArchitectureFitnessTest {

  static JavaClasses classes;

  @BeforeAll
  static void setUp() {
    classes =
        new ClassFileImporter()
            .withImportOption(Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("de.bund.digitalservice.ris.search");
  }

  @Test
  void packagesShouldBeFreeOfCycles() {
    SliceRule rule =
        SlicesRuleDefinition.slices()
            .matching("de.bund.digitalservice.(**)")
            .should()
            .beFreeOfCycles();
    rule.check(classes);
  }
}
