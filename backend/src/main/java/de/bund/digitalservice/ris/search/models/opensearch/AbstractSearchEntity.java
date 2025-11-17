package de.bund.digitalservice.ris.search.models.opensearch;

// Sonar detects a false positive cyclic dependency between the interface and
// the classes that implement it
@SuppressWarnings("javaarchitecture:S7027")
public sealed interface AbstractSearchEntity
    permits AdministrativeDirective, CaseLawDocumentationUnit, Literature, Norm {}
