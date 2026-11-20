---
title: Use cases
---

# Use cases

Short recipes for the most common tasks. Each page states the goal, the requests you need and the pitfalls we know of.

| I want to …                                             | Document kind | Go to                                                     |
| ------------------------------------------------------- | ------------- | --------------------------------------------------------- |
| find legislation by keyword or abbreviation             | Legislation   | [Find legislation](/use-cases/find-legislation/)          |
| get the version of a law that was valid on a given date | Legislation   | [Get a specific version](/use-cases/legislation-version/) |
| get a single article (§) of a law                       | Legislation   | [Get a single article](/use-cases/legislation-article/)   |
| find a court decision by file number, ECLI or court     | Case law      | [Find a court decision](/use-cases/find-court-decision/)  |
| choose between JSON, HTML, XML and ZIP                  | All           | [Choose a format](/use-cases/formats/)                    |
| download all documents of one kind                      | All           | [Download everything](/use-cases/bulk-download/)          |
| keep a local copy up to date                            | All           | [Stay up to date](/use-cases/stay-up-to-date/)            |

## Before you start

- Base URL: `https://testphase.rechtsinformationen.bund.de`. No API key is needed, see [Rate limiting](/guides/rate-limiting/).
- All examples use `curl -G` with `--data-urlencode`, so search terms with spaces or umlauts are encoded correctly.
- Some examples pipe the response into `jq` to show the relevant fields. You don't need jq to use the API.

## Endpoints by document kind

| Document kind             | Search                         | Single document                                 |
| ------------------------- | ------------------------------ | ----------------------------------------------- |
| Legislation               | `/v1/legislation`              | `/v1/legislation/eli/…`                         |
| Case law                  | `/v1/rechtsprechung`           | `/v1/rechtsprechung/{documentNumber}`           |
| Literature                | `/v1/literature`               | `/v1/literature/{documentNumber}`               |
| Administrative directives | `/v1/administrative-directive` | `/v1/administrative-directive/{documentNumber}` |
| All kinds at once         | `/v1/document`                 |                                                 |

::: tip Search across all document kinds
`/v1/document?searchTerm=…` searches legislation, case law and literature in one request. Use it if you don't know yet which kind of document you need.
:::
