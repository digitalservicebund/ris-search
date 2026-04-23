---
title: Legislation
hero:
  title: Legislation
  text: This resource provides access to current and historical version of legislation.
---

## Schema

<ClassContainer name="recht:Legislation" class="breakout" />

## Endpoints

### List all legislation

List all legislation in our database with support for [filtering](../../guides/filters/index.md) and [pagination](../../guides/pagination/index.md).

#### Parameters

<RequestParams method="get" path="/api/v1/legislation" />

<!--

#### Example

<DocumentedCodeExampleSection>
  <DocsPart>
    TODO docs
  </DocsPart>
  <CodePart>

<<< @/data/list-legislation.response.json

  </CodePart>
</DocumentedCodeExampleSection>

-->

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/legislation?pageIndex=3&limit=2
```

:::

<<< @/data/list-legislation.response.json

### Get a single legislation

Retrieve single a legislation by `@id`.

#### Parameters

<RequestParams method="get" path="/api/v1/legislation/{id}" />

#### Example

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/legislation/65e2fc71-3178-4d04-8d4a-5611365c05fa
```

:::

<<< @/data/get-legislation.response.json
