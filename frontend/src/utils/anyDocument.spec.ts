import { describe } from "vitest";
import type {
  AdministrativeDirective,
  AnyDocument,
  CaseLaw,
  DocumentEncodingSchema,
  LegislationExpression,
  Literature,
} from "~/types/api";
import {
  getIdentifier,
  isAdministrativeDirective,
  isCaselaw,
  isLegislation,
  isLiterature,
} from "./anyDocument";

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
        vorabdokument: false,
      };

      expect(isCaselaw(doc)).toBe(true);
    });

    it("returns false if the document is not a caselaw document", () => {
      const doc: LegislationExpression = {
        "@type": "Legislation",
        "@id": "4711",
        name: "",
        legislationIdentifier: "",
        exampleOfWork: {
          "@id": "",
          "@type": "Legislation",
          legislationIdentifier: "",
          legislationDate: "",
          datePublished: "",
          isPartOf: { name: "" },
        },
        alternateName: "",
        abbreviation: "",
        risAbbreviation: "",
        legislationLegalForce: "InForce",
        temporalCoverage: "",
        encoding: [],
        hasPart: [],
      };

      expect(isCaselaw(doc)).toBe(false);
    });
  });

  describe("isLegislation", () => {
    it("returns true if the document is a legislation work document", () => {
      const doc: LegislationExpression = {
        "@type": "Legislation",
        "@id": "4711",
        name: "",
        legislationIdentifier: "",
        exampleOfWork: {
          "@id": "",
          "@type": "Legislation",
          legislationIdentifier: "",
          legislationDate: "",
          datePublished: "",
          isPartOf: { name: "" },
        },
        alternateName: "",
        abbreviation: "",
        risAbbreviation: "",
        legislationLegalForce: "InForce",
        temporalCoverage: "",
        encoding: [],
        hasPart: [],
      };

      expect(isLegislation(doc)).toBe(true);
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
        vorabdokument: false,
      };

      expect(isLegislation(doc)).toBe(false);
    });
  });

  describe("isLiterature", () => {
    it("returns true if the document is a literature document", () => {
      const doc: Literature = {
        "@id": "4711",
        "@type": "Literature",
        inLanguage: "",
        documentNumber: "",
        yearsOfPublication: [],
        documentTypes: [],
        dependentReferences: [],
        independentReferences: [],
        headline: undefined,
        authors: [],
        collaborators: [],
        shortReport: undefined,
        outline: undefined,
        encoding: [],
        alternativeHeadline: "",
        conferenceNotes: [],
        headlineAdditions: "",
        languages: [],
        normReferences: [],
        originators: [],
        universityNotes: [],
        literatureType: "uli",
        editors: [],
        founder: [],
        publishers: [],
        publisherOrganizations: [],
        publishingHouses: [],
        edition: undefined,
        volumes: [],
        internationalIdentifiers: [],
      };

      expect(isLiterature(doc)).toBe(true);
    });

    it("returns false if the document is not a literature document", () => {
      const doc: LegislationExpression = {
        "@type": "Legislation",
        "@id": "4711",
        name: "",
        legislationIdentifier: "",
        exampleOfWork: {
          "@id": "",
          "@type": "Legislation",
          legislationIdentifier: "",
          legislationDate: "",
          datePublished: "",
          isPartOf: { name: "" },
        },
        alternateName: "",
        abbreviation: "",
        risAbbreviation: "",
        legislationLegalForce: "InForce",
        temporalCoverage: "",
        encoding: [],
        hasPart: [],
      };

      expect(isLiterature(doc)).toBe(false);
    });
  });

  describe("isAdministrativeDirective", () => {
    it("returns true if the document is an administrativeDirective document", () => {
      const doc = {
        "@type": "AdministrativeDirective",
      } as AdministrativeDirective;

      expect(isAdministrativeDirective(doc)).toBe(true);
    });

    it("returns false if the document is not an administrativeDirective document", () => {
      const doc = {
        "@type": "Legislation",
      } as LegislationExpression;

      expect(isAdministrativeDirective(doc)).toBe(false);
    });
  });

  describe("getEncodingURL", () => {
    const zipEncoding: Partial<DocumentEncodingSchema> = {
      encodingFormat: "application/zip",
      contentUrl: "/v1/placeholder/docNumber.zip",
    };
    const xmlEncoding: Partial<DocumentEncodingSchema> = {
      encodingFormat: "application/xml",
      contentUrl: "/v1/placeholder/docNumber.xml",
    };
    const htmlEncoding: Partial<DocumentEncodingSchema> = {
      encodingFormat: "text/html",
      contentUrl: "/v1/placeholder/docNumber.html",
    };
    const encodingArray = [
      zipEncoding,
      xmlEncoding,
      htmlEncoding,
    ] as DocumentEncodingSchema[];
    it("returns the URL for a matching format", () => {
      expect(getEncodingURL(encodingArray, "application/zip")).toBe(
        zipEncoding.contentUrl,
      );
      expect(getEncodingURL(encodingArray, "application/xml")).toBe(
        xmlEncoding.contentUrl,
      );
      expect(getEncodingURL(encodingArray, "text/html")).toBe(
        htmlEncoding.contentUrl,
      );
    });
    it("returns undefined for non-matching format", () => {
      expect(getEncodingURL(encodingArray, "application/json")).toBeUndefined();
    });

    it("returns undefined for null/undefined encoding array", () => {
      expect(getEncodingURL(null, "text/html")).toBeUndefined();
      expect(getEncodingURL(undefined, "text/html")).toBeUndefined();
    });
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
        vorabdokument: false,
      };

      expect(getIdentifier(doc)).toBe("4711");
    });

    it("identifies a legislation work document", () => {
      const doc: LegislationExpression = {
        "@type": "Legislation",
        "@id": "4712",
        name: "",
        legislationIdentifier: "4712",
        exampleOfWork: {
          "@id": "",
          "@type": "Legislation",
          legislationIdentifier: "",
          legislationDate: "",
          datePublished: "",
          isPartOf: { name: "" },
        },
        alternateName: "",
        abbreviation: "",
        risAbbreviation: "",
        legislationLegalForce: "InForce",
        temporalCoverage: "",
        encoding: [],
        hasPart: [],
      };

      expect(getIdentifier(doc)).toBe("4712");
    });

    it("identifies a literature document", () => {
      const doc: Literature = {
        "@id": "",
        "@type": "Literature",
        inLanguage: "",
        documentNumber: "4711",
        yearsOfPublication: [],
        documentTypes: [],
        dependentReferences: [],
        independentReferences: [],
        headline: undefined,
        authors: [],
        collaborators: [],
        shortReport: undefined,
        outline: undefined,
        encoding: [],
        alternativeHeadline: "",
        conferenceNotes: [],
        headlineAdditions: "",
        languages: [],
        normReferences: [],
        originators: [],
        universityNotes: [],
        literatureType: "uli",
        editors: [],
        founder: [],
        publishers: [],
        publisherOrganizations: [],
        publishingHouses: [],
        edition: undefined,
        volumes: [],
        internationalIdentifiers: [],
      };

      expect(getIdentifier(doc)).toBe("4711");
    });

    it("throws if the identifier is falsy", () => {
      const doc: Literature = {
        "@id": "",
        "@type": "Literature",
        inLanguage: "",
        documentNumber: "",
        yearsOfPublication: [],
        documentTypes: [],
        dependentReferences: [],
        independentReferences: [],
        headline: undefined,
        authors: [],
        collaborators: [],
        shortReport: undefined,
        outline: undefined,
        encoding: [],
        alternativeHeadline: "",
        conferenceNotes: [],
        headlineAdditions: "",
        languages: [],
        normReferences: [],
        originators: [],
        universityNotes: [],
        literatureType: "uli",
        editors: [],
        founder: [],
        publishers: [],
        publisherOrganizations: [],
        publishingHouses: [],
        edition: undefined,
        volumes: [],
        internationalIdentifiers: [],
      };

      expect(() => getIdentifier(doc)).toThrow("Failed to identify document");
    });

    it("throws if the document type is not supported", () => {
      expect(() =>
        getIdentifier({ "@type": "fake type" } as AnyDocument),
      ).toThrow("Failed to identify document");
    });
  });
});
