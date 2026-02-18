import type { JSONLDList, LegislationExpression } from "~/types";

function createWorkExample(
  expressionEli: string,
  temporalCoverage: string,
  legalForce: "InForce" | "NotInForce",
): LegislationExpression {
  return {
    "@type": "Legislation",
    "@id": `/v1/legislation/${expressionEli}`,
    legislationIdentifier: expressionEli,
    temporalCoverage: temporalCoverage,
    legislationLegalForce: legalForce,
    exampleOfWork: {
      "@type": "Legislation",
      legislationIdentifier: expressionEli.split("/").slice(0, 5).join("/"),
    },
    name: "",
    alternateName: "",
    legislationDate: "2025-01-01",
    datePublished: "2025-01-01",
    isPartOf: {
      name: "",
    },
    encoding: [],
    tableOfContents: [],
    hasPart: [],
  };
}
export const data: JSONLDList<LegislationExpression> = {
  "@type": "hydra:Collection",
  totalItems: 3,
  member: [
    createWorkExample(
      "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
      "2000-01-05/2019-12-31",
      "NotInForce",
    ),
    createWorkExample(
      "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1",
      "2020-01-01/..",
      "InForce",
    ),
    createWorkExample(
      "eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
      "2031-01-01/..",
      "NotInForce",
    ),
  ],
  view: {
    first: "",
    previous: null,
    next: null,
    last: "",
  },
};
