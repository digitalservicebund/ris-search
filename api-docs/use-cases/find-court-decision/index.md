---
title: Find a court decision
---

# Find a court decision

**Goal:** Find decisions by file number (Aktenzeichen), ECLI, court, date or keyword, then get the full text.

**Endpoint:** `GET /v1/rechtsprechung`

## 1. Search by file number

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung \
  --data-urlencode 'fileNumber=VIII ZR 123/23'
```

## 2. Search by ECLI

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung \
  --data-urlencode 'ecli=ECLI:DE:BGH:2024:120324UVIIIZR123.23.0'
```

## 3. All decisions of a court in a period, newest first

`court` accepts long and short names (`Bundesgerichtshof`, `BGH`) as well as court types (`Finanzgericht`, `FG`). `dateFrom` and `dateTo` refer to the decision date.

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung \
  --data-urlencode 'court=BGH' \
  --data-urlencode 'dateFrom=2024-01-01' \
  --data-urlencode 'dateTo=2024-12-31' \
  --data-urlencode 'sort=-date'
```

To see which court names are valid and how many decisions each court has:

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/courts \
  --data-urlencode 'prefix=Bundes'
```

## 4. Keyword search, only judgments

`typeGroup` accepts `Urteil`, `Beschluss` and `other`. Use `type` for the exact document type (e.g. `Versäumnisurteil`).

```bash
curl -G https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung \
  --data-urlencode 'searchTerm=Urlaub Arbeitnehmer' \
  --data-urlencode 'typeGroup=Urteil'
```

## 5. Get the decision

Every search result contains a `documentNumber`. It is the stable ID of a decision.

```bash
# Metadata and full text as JSON
curl https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/KVRE461542501
# HTML for display
curl https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/KVRE461542501.html
# XML (LegalDocML)
curl https://testphase.rechtsinformationen.bund.de/v1/rechtsprechung/KVRE461542501.xml
```

## Pitfalls

- Not every decision has an ECLI. Use `documentNumber` to store or link decisions.
- Search results use English field names (`documentNumber`, `fileNumbers`, `courtName`). The single document (`/v1/rechtsprechung/{documentNumber}`) uses German field names (`dokumentNummer`, `aktenzeichenListe`, `leitsatz`, `tenor`, `gruende`).
