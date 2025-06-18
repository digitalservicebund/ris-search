import type { JSONLDList, LegislationWork, SearchResult } from "~/types";

export const data: JSONLDList<SearchResult<LegislationWork>> = {
  "@type": "hydra:Collection",
  totalItems: 3,
  member: [
    {
      item: {
        "@type": "Legislation",
        "@id": "/v1/legislation/eli/bund/bgbl-1/2000/s001/regelungstext-1",
        name: "",
        legislationIdentifier: "eli/bund/bgbl-1/2000/s001/regelungstext-1",
        alternateName: "",
        abbreviation: "",
        legislationDate: "2000-01-01",
        datePublished: "2000-01-01",
        isPartOf: {
          name: "",
        },
        workExample: {
          "@type": "Legislation",
          "@id":
            "/v1/legislation/eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
          legislationIdentifier:
            "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
          temporalCoverage: "2000-01-05/2019-12-31",
          legislationLegalForce: "NotInForce",
          encoding: [],
          tableOfContents: [],
          hasPart: [],
        },
      },
      textMatches: [],
    },
    {
      item: {
        "@type": "Legislation",
        "@id": "/v1/legislation/eli/bund/bgbl-1/2000/s001/regelungstext-1",
        name: "",
        legislationIdentifier: "eli/bund/bgbl-1/2000/s001/regelungstext-1",
        alternateName: "",
        abbreviation: "",
        legislationDate: "2000-01-01",
        datePublished: "2000-01-01",
        isPartOf: {
          name: "",
        },
        workExample: {
          "@type": "Legislation",
          "@id":
            "/v1/legislation/eli/bund/bgbl-1/2000/s001/2020-01-01/2/deu/regelungstext-1",
          legislationIdentifier:
            "eli/bund/bgbl-1/2000/s001/2020-01-01/2/deu/regelungstext-1",
          temporalCoverage: "2020-01-01/..",
          legislationLegalForce: "InForce",
          encoding: [],
          tableOfContents: [],
          hasPart: [],
        },
      },
      textMatches: [],
    },
    {
      item: {
        "@type": "Legislation",
        "@id": "/v1/legislation/eli/bund/bgbl-1/2000/s001/regelungstext-1",
        name: "",
        legislationIdentifier: "eli/bund/bgbl-1/2000/s001/regelungstext-1",
        alternateName: "",
        abbreviation: "",
        legislationDate: "2000-01-01",
        datePublished: "2000-01-01",
        isPartOf: {
          name: "",
        },
        workExample: {
          "@type": "Legislation",
          "@id":
            "/v1/legislation/eli/bund/bgbl-1/2000/s001/2030-01-01/2/deu/regelungstext-1",
          legislationIdentifier:
            "eli/bund/bgbl-1/2000/s001/2030-01-01/2/deu/regelungstext-1",
          temporalCoverage: "2031-01-01/..",
          legislationLegalForce: "NotInForce",
          encoding: [],
          tableOfContents: [],
          hasPart: [],
        },
      },
      textMatches: [],
    },
  ],
  view: {
    first: "",
    previous: null,
    next: null,
    last: "",
  },
};
