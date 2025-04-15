# 5. Universal Search API Endpoint

Date: 2024-08-21

## Status

Accepted

## Context

The existing API design includes separate endpoints for universal search, norms, and caselaw. This structure is
inefficient when a user needs to make a mixed query (e.g., all types of norms plus just caselaw “Beschlüsse”). To handle
such requests, the application must make multiple API calls and merge the results, leading to increased complexity and
inefficiency.

## Decision Drivers

- The need for a more efficient and flexible API that can handle mixed queries.
- Desire to maintain a clean and readable API documentation.
- Simplifying the maintenance of API services.

## Considered Options

- Maintain separate endpoints: Continue with the current structure, where the frontend must handle multiple API calls
  for mixed queries.
- Introduce a universal search endpoint: Create a new endpoint that supports the union of caselaw and norms parameters,
  allowing for mixed queries in a single request.

## Decision Outcome

**Chosen Option: Introduce a Universal Search Endpoint**

We will implement a universal search endpoint that can handle mixed queries by accepting parameters for both caselaw and
norms in a single API call. This endpoint will return a list of mixed results, similar to the existing
`AllDocumentsSearchController`.

### Key Changes:

- New Universal Search Endpoint:
    - This endpoint will accept a UniversalSearchRequest object, encompassing parameters for both caselaw and norms,
      along with pagination settings.
    - The endpoint will be part of the `AllDocumentsSearchController`.
- ParameterObjects for Modularity:
    - We will use different `@ParameterObject` annotations in Spring to modularize the parameters, reducing duplication
      across multiple endpoints.
    - These include `UniversalSearchParams`, `NormsSearchParams`, `CaseLawSearchParams`, and `PaginationParams`.
- Consolidated Service Layer:
    - The `AllDocumentsService` will merge the functionality of the existing search services for caselaw, norms, and
      universal searches. This service will use `ElasticsearchOperations::search` to execute the queries and return a
      mixed list of results.
- Resource-focused endpoint structure
  - New endpoints like `/api/v1/document`, `/api/v1/legislation`, and `/api/v1/case-law` will be introduced, with `/api/v1/search` renamed to `/api/v1/document` for more resource-focused naming.

## Rationale

1. **Flexibility**: The API becomes more flexible, allowing for a broader range of query types.
2. **Simplification**: The frontend will only need to interact with a single universal search endpoint for most queries.
3. **Maintainability**: Fewer classes and services to maintain, as the universal search service consolidates multiple functionalities.

## Consequences

- API documentation: Different descriptions can be provided for the same fields in different endpoints using `@Schema`.
- The list and filter endpoints will be merged. Users can omit the searchTerm parameter to get a simple list or supply it for a filtered set of results.
