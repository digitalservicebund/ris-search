import type { JSONLDList, LegislationExpression } from "~/types/api";

function createLegislationExpression(
  expressionEli: string,
  temporalCoverage: string,
  legalForce: "InForce" | "NotInForce",
): LegislationExpression {
  const workIdentifier = expressionEli.split("/").slice(0, 5).join("/");
  return {
    "@type": "Legislation",
    "@id": `/v1/legislation/${expressionEli}`,
    legislationIdentifier: expressionEli,
    temporalCoverage: temporalCoverage,
    legislationLegalForce: legalForce,
    exampleOfWork: {
      "@id": `/v1/legislation/${workIdentifier}`,
      "@type": "Legislation",
      legislationIdentifier: workIdentifier,
      legislationDate: "2025-01-01",
      datePublished: "2025-01-01",
      isPartOf: {
        name: "",
      },
    },
    name: "",
    alternateName: "",
    encoding: [],
    hasPart: [],
  };
}
export const data: JSONLDList<LegislationExpression> = {
  "@type": "hydra:Collection",
  totalItems: 3,
  member: [
    createLegislationExpression(
      "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu/regelungstext-1",
      "2000-01-05/2019-12-31",
      "NotInForce",
    ),
    createLegislationExpression(
      "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1",
      "2020-01-01/..",
      "InForce",
    ),
    createLegislationExpression(
      "eli/bund/bgbl-1/2000/s001/2030-01-01/1/deu/regelungstext-1",
      "2031-01-01/..",
      "NotInForce",
    ),
  ],
  view: {
    first: "",
    previous: undefined,
    next: undefined,
    last: "",
  },
};
