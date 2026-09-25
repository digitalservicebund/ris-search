---
title: Formats
---

# Resource Endpoints & Formats

Our API supports retrieving entities in **JSON, XML, or HTML**. By default, all responses are returned in JSON format unless a specific format is requested. The format can be chosen by appending an extension to the URL.

## Retrieving an Entity in JSON (Default)

By default, entities are returned in JSON format when making a request to the base URL. The response will include the `Content-Type: application/json` header.

**Example Request:**

```http
GET /entity/123
```

## Retrieving an Entity in XML:

To retrieve an entity in XML format, append `.xml` to the URL. The response will include the `Content-Type: application/xml` header.

**Example Request:**

```http
GET /entity/123.xml
```

## Retrieving an Entity in HTML:

For an HTML-rendered version of the entity, append `.html` to the URL. The response will have the `Content-Type: text/html` header, making it suitable for rendering in a browser.

**Example Request:**

```http
GET /entity/123.html
```

## Retrieving the Full Text of a Piece of Legislation (LegalDocML.de)

A common task is to retrieve the machine-readable full text of a norm in
[LegalDocML.de](https://standards.rechtsinformationen.bund.de/). This takes two steps,
because search results wrap the resource and the full-text URL lives inside the resource.

### 1. Unwrap search results

Members of a **search** collection (e.g. `GET /v1/legislation?searchTerm=…`) are
returned as `SearchResult` objects. The resource itself is nested under `item`:

```json
{
  "@type": "hydra:Collection",
  "totalItems": 40,
  "member": [
    {
      "@type": "SearchResult",
      "item": {
        "@type": "Legislation",
        "@id": "/v1/legislation/eli/bund/bgbl-1/…",
        "legislationIdentifier": "eli/bund/bgbl-1/…",
        "abbreviation": "…",
        "legislationLegalForce": "InForce",
        "encoding": [ … ]
      },
      "textMatches": [ … ]
    }
  ]
}
```

### 2. Follow the `encoding` array to the full text

Each resource carries an `encoding` array with one entry per available representation.
Pick the entry whose `encodingFormat` is `application/xml` to get the LegalDocML.de
document. Note that `contentUrl` is **host-relative** — resolve it against the API host:

```json
"encoding": [
  { "encodingFormat": "text/html",        "contentUrl": "/v1/legislation/eli/…/regelungstext-verkuendung-1.html" },
  { "encodingFormat": "application/xml",  "contentUrl": "/v1/legislation/eli/…/regelungstext-verkuendung-1.xml" },
  { "encodingFormat": "application/zip",  "contentUrl": "/v1/legislation/eli/…/2026-07-15.zip" }
]
```

::: code-group

```bash [cURL]
# 1. find the norm, read member[0].item.encoding
curl -sG https://testphase.rechtsinformationen.bund.de/v1/legislation \
  --data-urlencode "searchTerm=Urheberrecht" --data-urlencode "size=1"

# 2. fetch the LegalDocML.de full text (contentUrl resolved against the host)
curl -s https://testphase.rechtsinformationen.bund.de/v1/legislation/eli/…/regelungstext-verkuendung-1.xml
```

:::

The `application/zip` entry bundles the complete act, while the `application/xml` entry
returns the regulation text (`regelungstext`) with its `<akn:article>` / `§` structure.
