# Dump caselaw data to Opensearch

This service is responsible for transferring Caselaw data from PostgreSQL to OpenSearch. This operation, known as a "dump," is resource-intensive in terms of CPU and memory usage. It is crucial to perform this dump selectively, only when modifications are made to the Caselaw data model in the OpenSearch index.

To execute the dump, follow the steps outlined below:

1. Access the OpenSearch dashboard on the relevant OTC instance (either staging or production). In the Dev Tools console, use the provided command to delete all existing documents in the Caselaw index. Due to the substantial size of the documents, it may be necessary to execute the command multiple times.

```
POST caselaws/_delete_by_query
{
  "query": {
    "match_all":{}
  }
}
```

2. After removing all data from the Caselaw index, modify the `CaseLawImporter.java` class to enable dumping in both the production and staging environments. Remove the `@Profile({"default"})` condition from the class.

3. Commit and merge your changes, and allow time for the dump process to complete. The duration of this process is expected to be between one and two hours. During this period, ensure that no deployments to the ris-search service are performed.

4. Once the dump process is successfully completed, reintroduce the `@Profile({"default"})` condition to the `CaseLawImporter.java` class. Commit and merge your changes again.
