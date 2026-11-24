# Response fields

The fields that the API endpoints return per document kind, following
[ADR 28](../adr/0028-only-return-the-data-the-portal-needs.md): a search result
contains the metadata used in a filter or shown in the portal's search
results, a single document response contains the fields the portal displays on
the document page.

These are the fields the portal uses today (as of 2026-11-24), with their
current names. Once the German names of a document kind are clear, its list
is updated. When the portal needs another field, it is added here.

The JSON-LD fields (`@context`, `@id`, `@type`) are always included and not
listed.

## Case law

Search result:

- `headline`
- `courtName`
- `decisionDate`
- `fileNumbers`
- `documentType`
- `decisionName`
- `titleLine`
- `documentNumber`

Single document:

- `gericht`
- `spruchkoerper`
- `datum`
- `datumsTyp`
- `aktenzeichenListe`
- `ecli`
- `dokumenttyp`
- `kurztitel`
- `titelzeile`
- `entscheidungsnamen`
- `leitsatz` (only for the page description in search engines)
- `vorgehendeEntscheidungen`
- `nachgehendeEntscheidungen`
- `gesetzeskraft`
- `streitjahre`
- `vorabdokument`
- `encoding`

## Norms

Search result:

- `name`
- `abbreviation`
- `alternateName`
- `legislationIdentifier`
- `exampleOfWork.legislationDate`
- `temporalCoverage`

Single document:

- `name`
- `abbreviation`
- `risAbbreviation`
- `alternateName`
- `legislationIdentifier`
- `exampleOfWork.legislationIdentifier`
- `exampleOfWork.legislationDate`
- `temporalCoverage`
- `legislationLegalForce`
- `fullCitation`
- `hasPart`
- `encoding`

## Literature

Search result:

- `headline`
- `alternativeHeadline`
- `documentTypes`
- `yearsOfPublication`
- `dependentReferences`
- `independentReferences`
- `documentNumber`

Single document:

- `documentNumber`
- `headline`
- `headlineAdditions`
- `alternativeHeadline`
- `documentTypes`
- `literatureType`
- `yearsOfPublication`
- `edition`
- `volumes`
- `internationalIdentifiers`
- `authors`
- `collaborators`
- `editors`
- `founder`
- `publishers`
- `publisherOrganizations`
- `publishingHouses`
- `originators`
- `conferenceNotes`
- `universityNotes`
- `dependentReferences`
- `independentReferences`
- `normReferences`
- `encoding`
