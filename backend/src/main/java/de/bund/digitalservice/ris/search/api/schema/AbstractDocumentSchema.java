package de.bund.digitalservice.ris.search.api.schema;

public sealed interface AbstractDocumentSchema
    permits CaseLawSearchSchema, LegislationWorkSearchSchema {}
