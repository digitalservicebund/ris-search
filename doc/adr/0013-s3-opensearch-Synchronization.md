# 13. S3 Buckets and OpenSearch Synchronization

Date: 2025-05-05

## Status

Accepted

## Context

We have two S3 buckets containing XML files that serve as the data foundation for our OpenSearch cluster. Source systems
write new data to those S3 buckets when changes occur, and delete stale items from them. The buckets are structured as
follows:

- Case Law Bucket: Stores legal case data.
- Legislation (Norms) Bucket: Stores legislative data.

Each bucket also contains changelog files in JSON format, which provide information about modifications to the data.
These files are structured as follows:

- Filename format: `<timestamp in UTC timezone>-<documentkind>.json` (where timestamp is
  in [ISO 8601 format](https://en.wikipedia.org/wiki/ISO_8601), and documentkind is `norms` or `caselaw`)

- Content structure:
    - If only some files have changed:
      `{"changed": [<list of file identifiers>], "deleted": [<list of file identifiers>]}`
    - If all files have changed: `{"change_all": true}`

To keep OpenSearch synchronized, changes must be regularly processed. We deliberately do not use S3 event notifications
or a message queue, to maintain provider independence and reduce operational complexity. Instead, change detection is
based on regular polling and changelog processing.

## Problem Statement

We need a mechanism to:

- Regularly detect changes in the S3 buckets.
- Efficiently process and synchronize these changes with the OpenSearch cluster.
- Hangle changelog files in order (implies no concurrency).
- Handle errors gracefully without disrupting the import process.
- Minimize manual human interactions.

## Decision

We will implement a **cron-based import process**, one for each bucket:

### 1. Triggering the Import Service

A Kubernetes CronJob runs in a fixed interval to process new data.

### 2. Processing Changelog Files

The job scans the bucket for unprocessed changelogs.

- The import service gets the **timestamp of the last processed changelog file** from a status entry in an OpenSearch application-level metadata index.
- It lists all changelog objects in the relevant S3 bucket with timestamps newer than the status file’s timestamp.
- Changelog files are sorted chronologically and processed sequentially.
- Upon successful processing, the timestamp (in UTC, ISO 8601 format) of the last processed file is written back to the
  OpenSearch index.

### 3. Error Handling

Given the needs listed above, we have decided on the following compromise

- When encountering known errors
    - When the job sees S3 downtime or Opensearch cluster downtime, it will **log an error, fail the current run of the
      job** and let the same file get tried again on the next job run (currently every 5 minutes).
    - When a changelog file contains the same file identifier as both "changed" and "deleted", the import process will *
      *log an error and ignore the invalid changelog file**.
    - Non-XML files are silently ignored. When a changelog file's name or content is unable to be parsed (for example, it's not valid JSON), the import process will **log an error and ignore the invalid changelog file**.
- Unknown errors
    - If an unknown error occurs while processing a change event from the changelog file, the import process will **log
      an error and continue with the next event**.
    - if an error occurs while processing a delete event from the changelog file, the import process will **log an error
      and start a full reindex**. Specifically the job will save the current timestamp, query the bucket for all files
      currently present, index all the files, then set the last success timestamp in the status entry to the timestamp at
      the start of the reindex. A full reindex is always safe to do, and we estimate this 2-3 hour operation is an
      acceptable cost to recover automatically without the need for a human to manually intervene.

### 4. Infrastructure

- This application (the importer) will have read-only access to the data buckets.
- The importer’s state (timestamp of last successful import) will be stored in a separate OpenSearch index.

## Consequences

- Ensures regular updates to OpenSearch.
- Prevents concurrent imports from interfering.
- Handles partial and full data updates efficiently.
- Provides error logging and retry mechanisms.
- Relies on cron job frequency, which may lead to minor delays in data synchronization.
