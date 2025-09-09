# Swapping indices in OpenSearch

Sometimes, the indexes in Opensearch need to be recreated.
This is usually the result of the *mappings.json and/or the german_analyzer.json files changing.
To prevent downtime, it is advisable to build a new index in the background and swap the indices once ready.
The following commands in this file should be run in the opensearch dev console for the appropriate environment being updated.

## Initial state

In staging and prototype `norms` and `caselaws` are not actually indexes, they are aliases.
Using an alias helps with a zero downtime index recreation.
Production and UAT should also use aliases instead of indexes, but this is not done yet.

## Make sure everything is the way you expect
There should already be indexes in the form `caselaws_OLD_DATE` and `norms_OLD_DATE`.
```shell
GET _cat/indices
GET _cat/aliases
```

## Update the common_settings_component
If german_analyzer.json changed then run this
```shell
PUT /_component_template/common_settings_component
{
  "template": {
    "settings":
    <<Paste entire content of german_analyzer.json here>>
  }
}
```

## Update the norms_mappings_component
If norms_mapping.json changed then run this
```shell
PUT /_component_template/norms_mappings_component
{
  "template": {
    "mappings":
    <<Paste entire content of norms_mapping.json here>>
  }
}
```

## Update the caselaw_mappings_component
If caselaw_mappings.json changed then run this
```shell
PUT /_component_template/caselaw_mappings_component
{
  "template": {
    "mappings":
    <<Paste entire content of caselaw_mappings.json here>>
  }
}
```

## Create the norms and caselaws templates
This only needs to be run if the templates don't already exist in the environement being updated.
```shell
PUT /_index_template/norms_template
{
  "index_patterns": ["norms*"],
  "composed_of": [
    "common_settings_component",
    "norms_mappings_component"
  ]
}

PUT /_index_template/caselaws_template
{
  "index_patterns": ["caselaws*"],
  "composed_of": [
    "common_settings_component",
    "caselaw_mappings_component"
  ]
}
```

## Create the new indexes
NEW_DATE should be today's date in the format YYYY-MM-DD
```shell
PUT /norms_NEW_DATE
PUT /caselaws_NEW_DATE
```

## Do the data copy
```shell
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

POST /_reindex?wait_for_completion=false
{
  "source": {
    "index": "caselaws_OLD_DATE"
  },
  "dest": {
    "index": "caselaws_NEW_DATE"
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
GET norms_OLD_DATE/_count
GET norms_NEW_DATE/_count
```

## Switch aliases to new indexes
```shell
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
        { "remove": { "index": "caselaws_OLD_DATE", "alias": "caselaws" } },
        { "add": { "index": "caselaws_NEW_DATE", "alias": "caselaws" } }
    ]
}
POST /_aliases
{
    "actions": [
        { "remove": { "index": "norms_OLD_DATE", "alias": "documents" } },
        { "add": { "index": "norms_NEW_DATE", "alias": "documents" } },
        { "remove": { "index": "caselaws_OLD_DATE", "alias": "documents" } },
        { "add": { "index": "caselaws_NEW_DATE", "alias": "documents" } }
    ]
}
```

## More double checks
Check the portal ui by searching and making sure everything is as expected.

## Delete the old indexes
```shell
DELETE norms_OLD_DATE
DELETE caselaws_OLD_DATE
```
