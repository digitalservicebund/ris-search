---
title: Filters
hero:
  title: Filters
  text: Learn how to filter responses to return a subset of entities.
---

## Full-Text Search Filtering

List endpoints support filtering results using the `searchTerm` query parameter. This parameter enables powerful full-text searches across all relevant text fields of the entities being listed.

### **Functionality:**

- **All-Term Matching (Default Behavior):**  
  When you enter multiple words without quotation marks, the search will return documents containing all the search terms, though not necessarily in the specified order.  
  **Example:**  
  `searchTerm=trademarks protection Hannover`  
  This will match documents containing any variation of "trademarks," "protection," and "Hannover."

- **Exact Phrase Matching:**  
  To search for an exact phrase, enclose it in double quotes (`"`). This ensures that only documents containing the exact phrase, in the specified order, are matched.  
  **Example:**  
  `searchTerm="trademarks protection"`  
  This will match documents containing the exact phrase "trademarks protection".

  > **Note:** Using quotation marks tells the search engine to look for the exact words in the exact order without any variations. This is particularly useful when searching for specific names, titles, or phrases.

### **Usage:**

- Append `searchTerm=<your_search_term>` to the endpoint URL.
- Ensure your `searchTerm` is properly URL-encoded to handle special characters and spaces.

### **Examples:**

- `searchTerm=urlaub arbeitnehmer`  
  Matches documents containing any variation of "urlaub," and "arbeitnehmer".

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/case-law?searchTerm=urlaub+arbeitnehmer
```

:::

- `searchTerm="urlaub arbeitnehmer"`  
  Matches documents containing the exact phrase "urlaub arbeitnehmer" and returns fewer results than the previous examples, as it enforces the word order to be "urlaub" followed by "arbeitnehmer."

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/case-law?searchTerm="urlaub arbeitnehmer"
```

:::

## Date Filters

All date fields implement similar query parameters for filtering.

- Ensure that all dates provided adhere to the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) format, as this is the
  only accepted format for date inputs
- Ranges are always inclusive–this means that dates that match the start or end are included in the result

| Query Parameter   | Type   | Description                                                                                                                |
|-------------------|--------|----------------------------------------------------------------------------------------------------------------------------|
| `<dateField>From` | `date` | The from (greater than or equal) operator returns all entities where `dateField` is later than, or equal to, a given date. |
| `<dateField>To`   | `date` | The to (less than or equal) operator returns all entities where `dateField` is earlier than, or equal to, a given date.    |

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/case-law?dateFrom=2023-12-30
curl -G https://testphase.rechtsinformationen.bund.de/v1/case-law?dateTo=2021-12-30
```