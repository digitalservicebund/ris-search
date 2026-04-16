---
title: Standards
outline: false
hero:
  variant: secondary
  title: Standards
  text: To ensure high data quality and interoperability, we follow established standards. We provide an overview on this page.
---
To ensure efficient access to legal information, we use various data formats in our portal: JSON, LegalDocML and
LegalDocML.de. These formats are used to provide data in a structured way, enable the exchange between systems and
promote the standardization of legal documents. Below we explain the individual formats and their areas of application.

## LegalDocML and LegalDocML.de

We use the XML-based LegalDocML and LegalDocML.de standard to structure legal documents. It defines how the content of
legal texts (such as laws and case law) should be structured in order to make them machine-readable and
interoperable.

The LegalDocML.de standard is a subset of the LegalDocML standard adapted to the German legal enactment of the Federal
government („Bundesrechtsetzung”). The standard is developed as part of the project E-Gesetzgebung by the Federal
Ministry of the Interior.

We use the two variants of LegalDocML as follows:

- For the provision of norms, we use the specific German implementation LegalDocML.de.
- For case law, we use the international LegalDocML standard, as there is no dedicated German version.

An understanding of LegalDocML is required to analyze the full dataset. We are currently working on comprehensive
documentation to help you get started. In the meantime, you can use the following resources:

- [LegalDocML.de documentation for norms](https://gitlab.opencode.de/bmi/e-gesetzgebung/ldml_de)
- [LegalDocML documentation for case law](https://docs.oasis-open.org/legaldocml/akn-core/v1.0/akn-core-v1.0-part1-vocabulary.html)
- [Learn more about the legislative cycle and the project E-Gesetzgebung](https://www.cio.bund.de/Webs/CIO/DE/digitale-loesungen/it-konsolidierung/dienstekonsolidierung/it-massnahmen/e-gesetzgebung/e-gesetzgebung-node.html)

## European Identifiers ELI and ECLI

The European Identifiers ELI and ECLI play a crucial role by providing a standardized way to identify legislative
documents across Europe.
ELI offers a standardized system for identifying legislation. It's designed to make legislative information more
accessible and easier to exchange. ELI uses a combination of elements to create a unique identifier for a piece of
legislation, including information about the issuing institution, the date of enactment, and a unique identifier within
that context.

ECLI provides a standardized format for identifying court decisions. Its purpose is to make case law easier to find and
cite. By using a consistent set of metadata elements, ECLI enables unambiguous identification of judgments regardless of
the court, language, or jurisdiction.

Please note that due to historic reasons not all judgments have a well-defined ECLI. For this reason judgments can only
be uniquely identified by their document number.

## Schema.org

[Legislation](https://schema.org/Legislation) is a specific schema within [schema.org](https://schema.org/) designed to describe legislative
documents on the web. It provides a  set of properties (attributes) that can be used to specify details about a piece of
legislation, such as:

- `legislationIdentifier`: A unique identifier for the legislation (e.g., an ELI).
- `name`: The official title of the document (Amtliche Langüberschrift).

The schema provides a summary of key information about a legislative document, making it easier for search engines to
index and understand. This provides a basic level of structured data about a legal document on a web page, while
LegalDocML.de provides a much richer and more detailed representation of the document itself. 
