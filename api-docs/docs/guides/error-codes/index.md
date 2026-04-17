---
title: Error Codes
hero:
  title: Error Codes
  text: Learn how to diagnose and resolve common problems.
---

In the event of an unsuccessful API request, standard [HTTP response codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status) are returned to indicate the error in the response header.

For most resources, additional error information is returned in the response body. In those cases, the error response body has the following structure:

```http
HTTP/1.1 400
Content-Type: application/json

{
    "errors": [
        {"code": <code>, "message": <message>, "parameter": <parameter>}
    ]
}
```
### Examples

#### 403 error - Response:

```
curl -X POST "https://testphase.rechtsinformationen.bund.de/v1/case-law"
```

```http
HTTP/2 403
Content-Type: application/json

{
  "errors": [
    {
      "code": "forbidden",
      "message": "Access is not allowed. This includes some cases of improper access such as wrong method.",
      "parameter": ""
    }
  ]
}
```

#### 404 error - Response:

```
curl "https://testphase.rechtsinformationen.bund.de/v1/DOES_NOT_EXIST"
```

```http
HTTP/2 404
Content-Type: application/json

{
  "errors": [
    {
      "code": "not_found",
      "message": "The requested data could not be found",
      "parameter": ""
    }
  ]
}
```

#### 422 Unprocessable Content

```
curl "https://testphase.rechtsinformationen.bund.de/v1/case-law?dateFrom=a&dateTo=a"
```

```http
HTTP/2 422
Content-Type: application/json

{
  "errors": [
    {
      "code": "invalid_parameter_value",
      "message": "Parameter value is invalid",
      "parameter": "dateFrom"
    },
    {
      "code": "invalid_parameter_value",
      "message": "Parameter value is invalid",
      "parameter": "dateTo"
    }
  ]
}
```

#### 500 Internal server error

```
unknown request
```

```http
HTTP/2 500
Content-Type: application/json

{
  "errors": [
    {
      "code": "internal_error",
      "message": "An unexpected error occurred. Please try again later.",
      "parameter": ""
    }
  ]
}
```

#### 503 Service Unavailable

```http
HTTP/2 503
```

The service might return a 503 status code if it is overloaded or under maintenance.

You might also get this staus code after making too many requests in a short time.
See [Rate Limiting](/guides/rate-limiting/) for more information.

<FeedbackSurvey id="018c252c-e5e6-0000-d66d-acd5f06b9541" context="en-guides-error-codes" />
