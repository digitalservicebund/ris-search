---
title: Pagination
hero:
  title: Pagination
  text: Learn how to navigate through paginated responses.
---

When the API returns a large number of results, it will paginate the results and return a subset of them. To navigate through paginated responses, you can use the `view.previous` and `view.next` fields from the response to request additional pages of data. If an endpoint supports the `size` query parameter, you can control how many results are returned on a page. The default size of the page is 100 entities.

The schema for paginated responses is based on the [`Collection`](https://www.hydra-cg.com/spec/latest/core/#collections) class from the [`Hydra vocabulary`](https://www.hydra-cg.com/spec/latest/core/#the-hydra-core-vocabulary-in-json-ld).

## Requests

All paginated endpoints share the following parameters:

| Query parameters                             | Type  | Description                       | Default |
|----------------------------------------------| ----- | --------------------------------- |---------|
| <CodeBadge tint="gray">size</CodeBadge>      | `int` | The number of entities per page   | 100     |
| <CodeBadge tint="gray">pageIndex</CodeBadge> | `int` | The number of the page to request | 0       |

## Responses

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/case-law
```

:::

<<< @/data/pagination-example.response.json

The response does include the following fields:

| Field           | Description                                    |
| --------------- | ---------------------------------------------- |
| `totalItems`    | The total number of entities available         |
| `member`        | The list of the entities returned in this page |
| `view.first`    | The URL of the first page                      |
| `view.previous` | The URL of the previous page                   |
| `view.next`     | The URL of the next page                       |
| `view.last`     | The URL of the last page                       |