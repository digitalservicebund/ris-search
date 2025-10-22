import {
  getIdentifier,
  isCaselaw,
  isLegislationWork,
  isLiterature,
} from "./anyDocument";
import type { CaseLaw, LegislationWork, Literature } from "~/types";

describe("anyDocument", () => {
  describe("isCaselaw", () => {
    it("returns true if the document is a caselaw document", () => {
      const doc: CaseLaw = {
        "@id": "4711",
        "@type": "Decision",
        documentNumber: "",
        ecli: "",
        decisionDate: "",
        fileNumbers: [],
        keywords: [],
        decisionName: [],
        deviatingDocumentNumber: [],
        inLanguage: "",
        encoding: [],
      };

      expect(isCaselaw(doc)).toBe(true);
    });

    it("returns false if the document is not a caselaw document", () => {
      const doc: LegislationWork = {
        "@type": "Legislation",
        "@id": "4711",
        name: "",
        legislationIdentifier: "",
        alternateName: "",
        legislationDate: "",
        datePublished: "",
        isPartOf: { name: "" },
        workExample: {
          "@type": "Legislation",
          "@id": "4712",
          legislationIdentifier: "",
          legislationLegalForce: "InForce",
          temporalCoverage: "",
          encoding: [],
          tableOfContents: [],
          hasPart: [],
        },
      };

      expect(isCaselaw(doc)).toBe(false);
    });
  });

  describe("isLegislation", () => {
    it("returns true if the document is a legislation work document", () => {
      const doc: LegislationWork = {
        "@type": "Legislation",
        "@id": "4711",
        name: "",
        legislationIdentifier: "",
        alternateName: "",
        legislationDate: "",
        datePublished: "",
        isPartOf: { name: "" },
        workExample: {
          "@type": "Legislation",
          "@id": "4712",
          legislationIdentifier: "",
          legislationLegalForce: "InForce",
          temporalCoverage: "",
          encoding: [],
          tableOfContents: [],
          hasPart: [],
        },
      };

      expect(isLegislationWork(doc)).toBe(true);
    });

    it("returns false if the document is not a legislation work document", () => {
      const doc: CaseLaw = {
        "@id": "4711",
        "@type": "Decision",
        documentNumber: "",
        ecli: "",
        decisionDate: "",
        fileNumbers: [],
        keywords: [],
        decisionName: [],
        deviatingDocumentNumber: [],
        inLanguage: "",
        encoding: [],
      };

      expect(isLegislationWork(doc)).toBe(false);
    });
  });

  it("returns true if the document is a literature document", () => {
    const doc: Literature = {
      "@id": "4711",
      "@type": "Literature",
      inLanguage: null,
      documentNumber: null,
      recordingDate: null,
      yearsOfPublication: [],
      documentTypes: [],
      dependentReferences: [],
      independentReferences: [],
      headline: null,
      alternativeTitle: null,
      authors: [],
      collaborators: [],
      shortReport: null,
      outline: null,
      encoding: [],
    };

    expect(isLiterature(doc)).toBe(true);
  });

  it("returns false if the document is not a literature document", () => {
    const doc: LegislationWork = {
      "@type": "Legislation",
      "@id": "4711",
      name: "",
      legislationIdentifier: "",
      alternateName: "",
      legislationDate: "",
      datePublished: "",
      isPartOf: { name: "" },
      workExample: {
        "@type": "Legislation",
        "@id": "4712",
        legislationIdentifier: "",
        legislationLegalForce: "InForce",
        temporalCoverage: "",
        encoding: [],
        tableOfContents: [],
        hasPart: [],
      },
    };

    expect(isLiterature(doc)).toBe(false);
  });

  describe("getIdentifier", () => {
    it("identifies a caselaw document", () => {
      const doc: CaseLaw = {
        "@id": "",
        "@type": "Decision",
        documentNumber: "4711",
        ecli: "",
        decisionDate: "",
        fileNumbers: [],
        keywords: [],
        decisionName: [],
        deviatingDocumentNumber: [],
        inLanguage: "",
        encoding: [],
      };

      expect(getIdentifier(doc)).toBe("4711");
    });

    it("identifies a legislation work document", () => {
      const doc: LegislationWork = {
        "@type": "Legislation",
        "@id": "",
        name: "",
        legislationIdentifier: "",
        alternateName: "",
        legislationDate: "",
        datePublished: "",
        isPartOf: { name: "" },
        workExample: {
          "@type": "Legislation",
          "@id": "4712",
          legislationIdentifier: "4712",
          legislationLegalForce: "InForce",
          temporalCoverage: "",
          encoding: [],
          tableOfContents: [],
          hasPart: [],
        },
      };

      expect(getIdentifier(doc)).toBe("4712");
    });

    it("identifies a literature document", () => {
      const doc: Literature = {
        "@id": "",
        "@type": "Literature",
        inLanguage: null,
        documentNumber: "4711",
        recordingDate: null,
        yearsOfPublication: [],
        documentTypes: [],
        dependentReferences: [],
        independentReferences: [],
        headline: null,
        alternativeTitle: null,
        authors: [],
        collaborators: [],
        shortReport: null,
        outline: null,
        encoding: [],
      };

      expect(getIdentifier(doc)).toBe("4711");
    });

    it("throws if the identifier is falsy", () => {
      const doc: Literature = {
        "@id": "",
        "@type": "Literature",
        inLanguage: null,
        documentNumber: null,
        recordingDate: null,
        yearsOfPublication: [],
        documentTypes: [],
        dependentReferences: [],
        independentReferences: [],
        headline: null,
        alternativeTitle: null,
        authors: [],
        collaborators: [],
        shortReport: null,
        outline: null,
        encoding: [],
      };

      expect(() => getIdentifier(doc)).toThrow();
    });

    it("throws if the document type is not supported", () => {
      // @ts-expect-error Deliberately providing an invalid type
      expect(() => getIdentifier({ "@type": "fake type" })).toThrow();
    });
  });
});
