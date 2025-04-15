# Swapping indices in OpenSearch

Sometimes, data needs to be re-indexed in OpenSearch, such as when the `index_options` are changed.
To prevent downtime, it is advisable to build a new index in the background and swap the indices once ready.

## Initial state

When the backend is connected to a new OpenSearch cluster instance, the main indices `norms` and `caselaws` are used.
A common alias `documents`, used to enable search across document kinds, points to both indices.

## Migration

When moving to a new index, make sure to use the old index name as a prefix. For global searches, this prefix
is used to determine how to process individual search hits.

Note that many changes can be performed without re-indexing.

### Create a new index

Using the dev console, a new index with (different) settings or mappings can be created using

```http request
PUT <index_name>

{
  "mappings": // replace with contents of norms_mapping.json or caselaw_mapping.json (object starting with key "properties")
  "settings": {
    "index": // replace with contents from german_analyzer.json (object starting with key "analysis")
  }
}
```

### Copy the data to the new index
```http request
POST _reindex

{
  "source": {"index":"norms" },
  "dest": {"index": "norms_2025-01-01"}
}
```

You may check on the progress by polling the tasks endpoint
```http request
GET _cat/tasks?actions=*reindex
```

Take appropriate measures to ensure that new data received by the application makes it to the new index.

The count can be checked by running

```http request
GET norms/_count
```
```http request
GET norms_2025-01-01/_count
```

### Replace original index by alias

```http request
POST _aliases

{
  "actions": [
    {
      "remove_index": {
        "index": "norms_or_caselaws"
      }
    },
    {
      "add": {
        "index": "norms_or_caselaws_2025-01-01",
        "alias": "norms_or_caselaws",
        "is_write_index": true
      }
    },
    {
      "add": {
        "index": "norms_or_caselaws_2025-01-01",
        "alias": "documents"
      }
    }
  ]
}
```

Specify `"is_write_index": true` to ensure that writes can be performed to the alias.
