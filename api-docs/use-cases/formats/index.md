---
title: Choose a format
---

# Choose a format

**Goal:** Pick the right format for your application.

| You want to … | Use | Why |
| --- | --- | --- |
| list, filter or link documents | JSON | Structured metadata, JSON-LD, links to all other formats |
| show a document to people | HTML | Ready to render, already structured into headings and paragraphs |
| process the full structure (analysis, own rendering, LLM pipelines) | XML | Complete document in LegalDocML (case law) or LegalDocML.de (legislation) |
| get the document with all attachments (images, annexes) | ZIP | XML plus all related files in one download |

## Available document formats

All document types are available in JSON, HTML, XML, and ZIP.

For legislation, you can retrieve different content depending on the format:

- **JSON:** A specific version of a law
- **HTML:** A complete file or an individual article (§)
- **XML:** A complete file
- **ZIP:** A complete manifestation of a law

Court decisions, literature, and administrative directives are available in all four formats.

## Case law, literature, administrative directives

Add an extension to the URL of the single document:

```bash
curl https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/KVRE461542501        # JSON
curl https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/KVRE461542501.html   # HTML
curl https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/KVRE461542501.xml    # XML
curl -O https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/KVRE461542501.zip # ZIP
```

The same pattern works for `/v1/literature/{documentNumber}` and `/v1/administrative-directive/{documentNumber}`.

## Legislation

JSON is available per version. HTML and XML are available per file of that version (e.g. `regelungstext-1`). Take the URLs from `encoding[].contentUrl` of the JSON response.

```bash
BASE=https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu
curl $BASE                                    # JSON: metadata and structure
curl $BASE/1998-01-29/regelungstext-1.html    # HTML
curl $BASE/1998-01-29/regelungstext-1.xml     # XML (LegalDocML.de)
curl -O $BASE/1998-01-29.zip                  # ZIP: all files of this version
```

## Pitfalls

- Search endpoints only return JSON. To get HTML or XML, follow the links in `encoding` or add the extension to the single document URL.
- For case law, the JSON of a single document already contains the full text fields (e.g. `tenor`, `gruende`). For legislation, the JSON contains metadata and structure, but not the text.
- More on the data standards: [Standards](/standards/).
