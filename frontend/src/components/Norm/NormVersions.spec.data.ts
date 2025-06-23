import type { JSONLDList, LegislationWork, SearchResult } from "~/types";

function createWorkExample(
  expressionEli: string,
  temporalCoverage: string,
  legalForce: "InForce" | "NotInForce",
): LegislationWork {
  return {
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
      "@id": `/v1/legislation/${expressionEli}`,
      legislationIdentifier: expressionEli,
      temporalCoverage: temporalCoverage,
      legislationLegalForce: legalForce,
      encoding: [],
      tableOfContents: [],
      hasPart: [],
    },
  };
}
export const data: JSONLDList<SearchResult<LegislationWork>> = {
  "@type": "hydra:Collection",
  totalItems: 3,
  member: [
    {
      item: {
        ...createWorkExample(
          "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
          "2000-01-05/2019-12-31",
          "NotInForce",
        ),
      },
      textMatches: [],
    },
    {
      item: {
        ...createWorkExample(
          "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1",
          "2020-01-01/..",
          "InForce",
        ),
      },
      textMatches: [],
    },
    {
      item: {
        ...createWorkExample(
          "eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
          "2031-01-01/..",
          "NotInForce",
        ),
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
