---
title: Search
hero:
  title: Search
  text: Use this endpoint to search across all entity types in our database.
---

Our cross-entity search leverages a robust full-text search mechanism in the background. The search is not limited to exact matches but is designed to search for related search terms.

## Schema

<ClassContainer name="recht:SearchResults" class="breakout">
  <ClassContainer name="recht:SearchResult" />
  <ClassContainer name="recht:SearchResultMatch" />
</ClassContainer>

## Endpoints

### Search all entities

This endpoint can be used to search for a specific item. For instance, you can find legislation that contains a particular search term. The endpoint provides up to 500 results for each search request.

The `searchTerm` parameter searches across multiple fields of an entity at the same time. The fields searched depend on the entity type. We are currently working on fine-tuning the algorithm and will publish a definite list later. See <a href="/guides/filters">the filters guide</a> for more information.

Results are sorted by best match in descending order unless another sort option is provided as a query parameter. Multiple factors are combined to boost the most relevant item to the top of the result list.

#### Parameters

<RequestParams method="get" path="/api/v1/search" />

#### Example

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/search?searchTerm=Helgoland
```

:::

<<< @/data/search.response.json
