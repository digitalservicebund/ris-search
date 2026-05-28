---
title: Get Started
---

# Getting Started with the API

Learn how to quickly connect to our legal information API and start building applications with German federal legislation and decisions by federal courts.

## Introduction

Welcome to the API documentation for accessing legal information and court decisions in Germany.
This guide will help you set up your environment and make your first request. Our API offers simple and efficient access to a broad collection of legal data.

## Prerequisites

- Basic knowledge of REST APIs.
- Familiarity with JSON and HTTP concepts.
- Familiarity with [standards](/standards/) such as:
  - [Legislation](https://schema.org/Legislation) Schema and [JsonLD](https://json-ld.org/)
  - LegalDocML Standard (see [Documentation for norms](https://gitlab.opencode.de/bmi/e-gesetzgebung/ldml_de) and [Documentation for case law](https://docs.oasis-open.org/legaldocml/akn-core/v1.0/akn-core-v1.0-part1-vocabulary.html))
  - European Legal Identifiers (ELI and ECLI)

## Making Your First Request

1. **Authentication:**
   At the moment, the API is open and does not require an API key. However, we recommend you to read more about [rate limiting](/guides/rate-limiting/) to ensure a smooth experience for the API.

2. **Formulating your request:**

The following example shows how to retrieve a single case law document from our API using Curl:

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/case-law/KVRE461542501
```

You can discover more endpoints with examples in [Swagger](https://docs.rechtsinformationen.bund.de/swagger-ui/index.html).

## Exploring the API

After your first request, check out the following sections for more detailed information:

- **API Overview:** Learn about the API operations and data formats from the [Home page](/).
- **Guides:** Get detailed instructions on topics such as [Formats](/guides/formats/), [Pagination](/guides/pagination/), [Filters](/guides/filters/), [Rate Limiting](/guides/rate-limiting/), and [Error Codes](/guides/error-codes/).
- **Standards:** Read more about the data formats and standards like LegalDocML in [Standards](/standards/).
- **Feedback:** Share your thoughts via our [Feedback](/feedback/) page.
- **Changelog:** Stay updated with changes in our [Changelog](/changelog/).
- **Contact:** For further questions or personalized help, visit our [Contact](/contact/) page.

## Next Steps

- Experiment with different requests and explore the API features.
- Use the guides to integrate our API into your applications.
- Provide feedback to help improve our service.

Enjoy building with our API!
We would be happy to see what you built with our API: [rechtsinformationen@digitalservice.bund.de](mailto:rechtsinformationen@digitalservice.bund.de)
