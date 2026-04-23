---
title: Export
hero:
  title: Export
  text: This resource provides an efficient way to retrieve entire datasets from our API. Whether conducting in-depth analysis or integrating data into your applications, this endpoint is the preferred way to do offline processing.
---

## Export Structure

The exported dataset is returned as a ZIP file with the following structure.

<FileExplorer>
  <FileExplorerItem type="folder" level="1" item="legislation">
    Which folders are included in the archive depends on the requested <code>types</code>.
  </FileExplorerItem>
  <FileExplorerItem type="folder" level="2" item="<partition>">
    We are in the process of defining partitions per type. For legislation, this may be the FNA, ELI, or the year of the announcement. We are happy to get some feedback on what the ideal folder structure for you would be.
  </FileExplorerItem>
  <FileExplorerItem type="file" level="3" item="<id>.xml">
    The entity encoded as LegalDocML file. Only included if you requested the <code>xml</code> encoding.
  </FileExplorerItem>
  <FileExplorerItem type="file" level="3" item="<id>.json">
    The entity encoded as JSON-LD file. Only included if you request the <code>json</code> encoding.
  </FileExplorerItem>
  <FileExplorerItem type="file" level="3" item="<id>.txt">
    The entity encoded as plain text file. Only included if you request the <code>text</code> encoding.
  </FileExplorerItem>
  <FileExplorerItem type="file" level="3" item="<id>.pdf">
    The entity encoded as a PDF file. Only included if you request the <code>pdf</code> encoding.
  </FileExplorerItem>
  <FileExplorerItem type="file" level="3" item="<id>.html">
    The entity encoded as a HTML file. Only included if you request the <code>html</code> encoding.
  </FileExplorerItem>
  <FileExplorerItem type="file" level="2" item="index_<n>.json">
    An index file that contains metadata about the exported entities. Depending on the number of exported entities, there may be multiple index files, and each file can reference up to 1,000 entities.
  </FileExplorerItem>
</FileExplorer>

## Endpoints

### Export entities

Request a data export as an archive, which is returned as a binary stream. Please note that the `dateModified` refers to the last modification of an expression. The modification can be triggered by many events, such as a lifecycle event (e.g., legislation went into effect) or an internal re-export of the encoding. We advise you to build your ingestion pipeline with incremental updates in mind.

#### Parameters

<RequestParams method="post" path="/api/v1/exports" />

#### Example

::: code-group

```bash [cURL]
curl -X https://testphase.rechtsinformationen.bund.de/v1/exports \
  -H 'Content-Type: application/json'
  -d '{ "types": ["Legislation"], "encodings": ["xml", "html"], dateModified_range: "2005-01-01,2023-01-01" }'
```

:::

```
(binary stream of a ZIP archive)
```
