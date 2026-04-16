---
title: formats
outline: [2, 2]
hero:
  title: Resource Endpoints & Formats
  text: Understand the formats we support
  
---
Our API supports retrieving entities in **JSON, XML, or HTML**. By default, all responses are returned in JSON format unless a specific format is requested. The format can be chosen by appending an extension to the URL.

### Retrieving an Entity in JSON (Default)
By default, entities are returned in JSON format when making a request to the base URL. The response will include the `Content-Type: application/json` header.

**Example Request:**
```http
GET /entity/123
```
### Retrieving an Entity in XML:
To retrieve an entity in XML format, append `.xml` to the URL. The response will include the `Content-Type: application/xml` header.

**Example Request:**
```http
GET /entity/123.xml
```
### Retrieving an Entity in HTML:
For an HTML-rendered version of the entity, append `.html` to the URL. The response will have the `Content-Type: text/html` header, making it suitable for rendering in a browser.

**Example Request:**
```http
GET /entity/123.html
```

