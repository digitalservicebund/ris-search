package de.bund.digitalservice.ris.search.schema;

public sealed interface AbstractDocumentSchema
    permits CaseLawSearchSchema, LegislationWorkSearchSchema {}
