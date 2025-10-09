# Swapping indices in OpenSearch

Sometimes, the indexes in Opensearch need to be recreated.
This is usually the result of the *mappings.json and/or the german_analyzer.json files changing.
To prevent downtime, it is advisable to build a new index in the background and swap the indices once ready.
The following commands in this file should be run in the opensearch dev console for the appropriate environment being updated.



## Initial state

`caselaws`, `literature` and `norms` are not actually indexes, they are aliases.
Using an alias helps with a zero downtime index recreation.

## Make sure everything is the way you expect
There should already be indexes in the form `<DOC_KIND>_OLD_DATE`.
```shell
GET _cat/indices
GET _cat/aliases
```

## Create the new indexes
NEW_DATE should be today's date in the format YYYY-MM-DD.
Please note that these index creation commands will automatically use the template files by name prefix.
```shell
PUT /caselaws_NEW_DATE
PUT /literature_NEW_DATE
PUT /norms_NEW_DATE
```

## Do the data copy
```shell
POST /_reindex?wait_for_completion=false
{
  "source": {
    "index": "caselaws_OLD_DATE"
  },
  "dest": {
    "index": "caselaws_NEW_DATE"
  }
}

POST /_reindex?wait_for_completion=false
{
  "source": {
    "index": "literature_OLD_DATE"
  },
  "dest": {
    "index": "literature_NEW_DATE"
  }
}

POST /_reindex?wait_for_completion=false
{
  "conflicts": "proceed",
  "source": {
    "index": "norms_OLD_DATE"
  },
  "dest": {
    "index": "norms_NEW_DATE"
  }
}
```

## Wait for the copy to finish
```shell
GET _cat/tasks?actions=*reindex
```

## Double checks
The count of the old and new indexes should match your expectations
```shell
GET caselaws_OLD_DATE/_count
GET caselaws_NEW_DATE/_count
GET literature_OLD_DATE/_count
GET literature_NEW_DATE/_count
GET norms_OLD_DATE/_count
GET norms_NEW_DATE/_count
```

## Switch aliases to new indexes
```shell
POST /_aliases
{
    "actions": [
        { "remove": { "index": "caselaws_OLD_DATE", "alias": "caselaws" } },
        { "add": { "index": "caselaws_NEW_DATE", "alias": "caselaws" } }
    ]
}

POST /_aliases
{
    "actions": [
        { "remove": { "index": "literature_OLD_DATE", "alias": "literature" } },
        { "add": { "index": "literature_NEW_DATE", "alias": "literature" } }
    ]
}

POST /_aliases
{
    "actions": [
        { "remove": { "index": "norms_OLD_DATE", "alias": "norms" } },
        { "add": { "index": "norms_NEW_DATE", "alias": "norms" } }
    ]
}

POST /_aliases
{
    "actions": [
        { "remove": { "index": "caselaws_OLD_DATE", "alias": "documents" } },
        { "add": { "index": "caselaws_NEW_DATE", "alias": "documents" } },
        { "remove": { "index": "literature_OLD_DATE", "alias": "documents" } },
        { "add": { "index": "literature_NEW_DATE", "alias": "documents" } },
        { "remove": { "index": "norms_OLD_DATE", "alias": "documents" } },
        { "add": { "index": "norms_NEW_DATE", "alias": "documents" } }
    ]
}
```

## More double checks
Check the portal ui by searching and making sure everything is as expected.

## Delete the old indexes
```shell
DELETE caselaws_OLD_DATE
DELETE literature_OLD_DATE
DELETE norms_OLD_DATE
```
