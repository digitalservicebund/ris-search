# 8. Use manifestations as the primary entity for legislation

Date: 2024-11-01

## Status

Accepted

## Context

There are different versions and revisions of a legislation document. The LegalDocML.de standard used in this project
uses the terms of the [Functional Requirements for Bibliographic Records (FRBR) model](https://en.wikipedia.org/wiki/Functional_Requirements_for_Bibliographic_Records) to define a hierarchy.

The key entities of FRBR are work, expression, manifestation, and item.

- A **work** is a distinct piece of legislation, e.g. the "Mindesturlaubsgesetz für Arbeitnehmer".
- An **expression** is a version of a _work_, at a specific point in time and in a specific language. For instance, the original published version would be an expression, as would every amended version of it.
- A **manifestation** is defined as the "physical embodiment of an Expression, either on paper or in any electronic format" ([link](http://data.europa.eu/eli/ontology#Manifestation)).

## Decision

The Legislation type returned by the API list and single-item endpoints is defined to refer to an _expression_, i.e., a version of a piece of legislation at a specific point in time.

## Consequences

A search for a specific work identifier in ELI format (the `akn:FRBRuri` of an `akn:FRBRWork`) like `eli/dl/2018/bundesregierung/34/regelungsentwurf` may return multiple matching expressions (e.g. `eli/dl/2018/bundesregierung/34/regelungsentwurf/bundestag/2018-12-18/7/deu`).

Each expression may refer to one or more manifestations, such as `eli/dl/2018/bundesregierung/34/regelungsentwurf/bundestag/2018-12-18/7/deu/regelungstext-1.xml`.

For the time being, no objects representing _works_, such as the abstract legislation item "Mindesturlaubsgesetz für Arbeitnehmer" across different versions, will be handled by the system or returned by the API.


### Example

Searching for "Mindesturlaubsgesetz für Arbeitnehmer", without specifying the point in time when the search results should be valid,
will return both the original version of 1963-01-08, the one from 2013-04-20, and potentially others.
