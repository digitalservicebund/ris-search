---
title: Use cases
---

# What would you like to do?

Find step-by-step guides to help you search, retrieve, and download legislation and court decisions.

[Find legislation](/use-cases/find-legislation)<Badge type="info" text="LEGISLATION" /><br> Search for legislation using a keyword or abbreviation.

---

[Retrieve legislation as of a specific date](/use-cases/legislation-version) <Badge type="info" text="LEGISLATION" /><br> Find the version of a law that was in force on a particular date.

---

[Retrieve a specific article (§)](/use-cases/legislation-article) <Badge type="info" text="LEGISLATION" /><br> Access a single article (§) of a law without retrieving the entire document.

---

[Find a court decision](/use-cases/find-court-decision) <Badge type="info" text="CASE LAW" /><br> Search for a court decision by case number, ECLI, or court.

---

[Choose a file format](/use-cases/formats) <Badge type="info" text="ALL DOCUMENTS" /><br> Choose between JSON, HTML, XML, and ZIP for your use case.

---

[Download documents in bulk](/use-cases/bulk-download) <Badge type="info" text="ALL DOCUMENTS" /><br> Download all available documents of a specific type.

---

[Keep your local data up to date](/use-cases/stay-up-to-date) <Badge type="info" text="ALL DOCUMENTS" /><br> Learn how to identify changes and update your locally stored documents.

## Before you start

- Base URL: `https://testphase.rechtsinformationen.bund.de`. No API key is needed, see [Rate limiting](/guides/rate-limiting/).
- All examples use `curl -G` with `--data-urlencode`, so search terms with spaces or umlauts are encoded correctly.
- Some examples pipe the response into `jq` to show the relevant fields. You don't need jq to use the API.

## Endpoints by document kind

| Document kind | Search | Single document |
| --- | --- | --- |
| Legislation | `/v1/legislation` | `/v1/legislation/eli/…` |
| Case law | `/v1/rechtsprechung` | `/v1/rechtsprechung/{documentNumber}` |
| Literature | `/v1/literature` | `/v1/literature/{documentNumber}` |
| Administrative directives | `/v1/administrative-directive` | `/v1/administrative-directive/{documentNumber}` |
| All kinds at once | `/v1/document` |  |

::: tip Search across all document kinds
`/v1/document?searchTerm=…` searches legislation, case law and literature in one request. Use it if you don't know yet which kind of document you need.
:::
