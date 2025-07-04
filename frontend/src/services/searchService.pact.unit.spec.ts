// @vitest-environment node
import type { MatchersV3 } from "@pact-foundation/pact";
import { PactV4, SpecificationVersion } from "@pact-foundation/pact";
import * as path from "node:path";
import fs from "fs";
import axios, { type AxiosResponse } from "axios";
import type { CaseLaw, LegislationWork } from "@/types";
import { describe, expect, it } from "vitest";

describe("GET /case-law/document", () => {
  // Create a 'pact' between the two applications in the integration we are testing
  const targetDir = path.resolve(process.cwd(), "../backend/pacts");

  it("writes to the correct directory", () => {
    expect(process.cwd()).toMatch(/frontend$/);
    const exists = fs.existsSync(targetDir);
    expect(exists).toBe(true);
  });

  const provider = new PactV4({
    dir: targetDir,
    consumer: "frontend",
    provider: "backend",
    spec: SpecificationVersion.SPECIFICATION_VERSION_V4, // Modify this as needed for your use case
  });

  // API Client that will fetch a single case-law document from the backend API
  // This is the target of our Pact test
  function getCaseLawDocument(
    server: MatchersV3.V3MockServer,
    documentNumber: string,
  ): Promise<AxiosResponse> {
    return axios.request({
      baseURL: server.url,
      headers: { Accept: "application/json" },
      method: "GET",
      url: `/v1/case-law/${documentNumber}`,
    });
  }

  function getNormDocument(
    server: MatchersV3.V3MockServer,
    url: string,
  ): Promise<AxiosResponse> {
    return axios.request({
      baseURL: server.url,
      headers: { Accept: "application/json" },
      method: "GET",
      url,
    });
  }

  interface Eli {
    prefix: string;
    subtype: string;
  }
  const workEli: Eli = {
    prefix: "eli/bund/bgbl-1/2000/s998/",
    subtype: "regelungstext-1",
  };
  const expressionEli: Eli = {
    prefix: workEli.prefix + "2000-10-06/2/deu/",
    subtype: "regelungstext-1",
  };
  const manifestationEli: Eli = {
    prefix: expressionEli.prefix + "2000-10-06/",
    subtype: "regelungstext-1",
  };
  const zipManifestationEliPrefix: Eli = {
    prefix: expressionEli.prefix + "2000-10-06",
    subtype: "",
  };

  function buildIdentifier(eli: Eli): string {
    return eli.prefix + eli.subtype;
  }
  function buildPath(eli: Eli): string {
    return "/v1/legislation/" + buildIdentifier(eli);
  }

  const normDocumentExample: LegislationWork = {
    "@id": buildPath(workEli),
    "@type": "Legislation",
    legislationIdentifier: buildIdentifier(workEli),
    name: "Verordnung uber die Nichtanwendung fleisch- und geflugelfleischhygienerechtlicher Vorschriften Artikel 6 der Funften Verordnung zur Anderung von Vorschriften zum Schutz der Verbraucher vor der Bovinen Spongiformen Enzephalopathie",
    alternateName:
      "Verordnung uber die Nichtanwendung fleisch- und geflugelfleischhygienerechtlicher Vorschriften",
    legislationDate: "1977-05-21",
    datePublished: "1977-05-22",

    abbreviation: "Fleischhygiene-Verordnung",

    isPartOf: {
      name: "Bundesgesetzblatt Teil I",
    },
    workExample: {
      "@type": "Legislation",
      "@id": buildPath(expressionEli),
      legislationIdentifier: buildIdentifier(expressionEli),
      legislationLegalForce: "InForce",
      temporalCoverage: "2001-05-29/..",
      hasPart: [
        {
          "@id": buildPath(expressionEli) + "#hauptteil-1_art-1",
          "@type": "Legislation",
          eId: "hauptteil-1_art-1",
          entryIntoForceDate: "2001-06-01",
          expiryDate: "2001-06-30",
          guid: null,
          isActive: false,
          name: "1 Fleischhygiene-Verordnung",
          encoding: null,
        },
        {
          "@id": buildPath(expressionEli) + "#hauptteil-1_art-2",
          "@type": "Legislation",
          eId: "hauptteil-1_art-2",
          entryIntoForceDate: null,
          expiryDate: null,
          guid: null,
          isActive: true,
          name: "2 Geflugelfleischhygiene-Verordnung",
          encoding: null,
        },
      ],
      tableOfContents: [
        {
          "@type": "TocEntry",
          id: "hauptteil-1_para-1",
          marker: "1",
          heading: "Art 1",
          children: [],
        },
        {
          "@type": "TocEntry",
          id: "hauptteil-1_para-2",
          marker: "2",
          heading: "Art 2",
          children: [],
        },
        {
          "@type": "TocEntry",
          id: "hauptteil-1_teil-1",
          marker: "Teil 1",
          heading: "Heading 1",
          children: [
            {
              "@type": "TocEntry",
              id: "hauptteil-1_teil-1-para-1",
              marker: "3",
              heading: "Art 3",
              children: [],
            },
            {
              "@type": "TocEntry",
              id: "hauptteil-1_teil-1_teil-1",
              marker: "Teil 2",
              heading: "Heading 2",
              children: [
                {
                  "@type": "TocEntry",
                  id: "hauptteil-1_teil-1_teil-1-para-1",
                  marker: "4",
                  heading: "Art 4",
                  children: [],
                },
                {
                  "@type": "TocEntry",
                  id: "hauptteil-1_teil-1_teil-1_teil-1",
                  marker: "Teil 3",
                  heading: "Heading 3",
                  children: [
                    {
                      "@type": "TocEntry",
                      id: "hauptteil-1_teil-1_teil-1_teil-1-para-1",
                      marker: "5",
                      heading: "Art 5",
                      children: [],
                    },
                    {
                      "@type": "TocEntry",
                      id: "hauptteil-1_teil-1_teil-1_teil-1-para-2",
                      marker: "6",
                      heading: "Art 6",
                      children: [],
                    },
                  ],
                },
              ],
            },
          ],
        },
      ],
      encoding: [
        {
          "@type": "LegislationObject",
          "@id": `${buildPath(manifestationEli)}/html`,
          contentUrl: `${buildPath(manifestationEli)}.html`,
          encodingFormat: "text/html",
          inLanguage: "de",
        },
        {
          "@type": "LegislationObject",
          "@id": `${buildPath(manifestationEli)}/xml`,
          contentUrl: `${buildPath(manifestationEli)}.xml`,
          encodingFormat: "application/xml",
          inLanguage: "de",
        },
        {
          "@type": "LegislationObject",
          "@id": `${buildPath(zipManifestationEliPrefix)}/zip`,
          contentUrl: `${buildPath(zipManifestationEliPrefix)}.zip`,
          encodingFormat: "application/zip",
          inLanguage: "de",
        },
      ],
    },
  };

  const caseLawDocumentExample: CaseLaw = {
    "@type": "Decision",
    "@id": "/v1/case-law/12345",
    documentNumber: "12345",
    ecli: "ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
    caseFacts: "Tatbestand",
    decisionGrounds: "Entscheidungsgrunde",
    dissentingOpinion: "Abweichende Meinung",
    grounds: "Grunde",
    guidingPrinciple: "Leitsatz",
    headline: "Uberschrift",
    headnote: "Orientierungssatz",
    otherHeadnote: "Sonstiger Orientierungssatz",
    otherLongText: "Sonstiger Langtext",
    tenor: "Tenor",
    decisionDate: "2023-10-11",
    fileNumbers: ["BGH 123/23", "BGH 124/23"],
    courtType: "FG",
    location: "Berlin",
    documentType: "Urteil",
    outline: "Leitsatz",
    judicialBody: "Gericht",
    keywords: ["keyword1", "keyword2", "keyword3"],
    courtName: "Bundesgerichtshof",
    decisionName: ["Decision Name 1", "Decision Name 2"],
    deviatingDocumentNumber: ["DEV-123", "DEV-124"],
    inLanguage: "de",
    encoding: [
      {
        "@type": "DecisionObject",
        "@id": "/v1/case-law/12345/html",
        contentUrl: "/v1/case-law/12345.html",
        encodingFormat: "text/html",
        inLanguage: "de",
      },
      {
        "@type": "DecisionObject",
        "@id": "/v1/case-law/12345/xml",
        contentUrl: "/v1/case-law/12345.xml",
        encodingFormat: "application/xml",
        inLanguage: "de",
      },
    ],
  };

  it("returns an HTTP 200 and a case law document", () => {
    // Arrange: set up our expected interactions
    // We use Pact to mock out the backend API
    return provider
      .addInteraction()
      .given("I have a document in the database with number 12345")
      .uponReceiving(
        "a request for retrieving the document with the same number",
      )
      .withRequest("GET", "/v1/case-law/12345", (builder) => {
        builder.headers({ Accept: "application/json" });
      })
      .willRespondWith(200, (builder) => {
        builder.headers({ "Content-Type": "application/json" });
        builder.jsonBody(caseLawDocumentExample);
      })
      .executeTest(async (mockserver) => {
        // Act: test our API client behaves correctly
        // Note we configure the GetCaseLawDocument API client dynamically to
        // point to the mock service Pact created for us, instead of the real one
        return await getCaseLawDocument(mockserver, "12345").then(
          (response) => {
            expect(response.data).to.deep.eq(caseLawDocumentExample);
          },
        );
      });
  });

  it("returns an HTTP 200 and a norm document", () => {
    return provider
      .addInteraction()
      .given(
        `I have a document in the database with ${buildIdentifier(expressionEli)}`,
      )
      .uponReceiving("a request for retrieving the document with the same eli")
      .withRequest("GET", buildPath(expressionEli), (builder) => {
        builder.headers({ Accept: "application/json" });
      })
      .willRespondWith(200, (builder) => {
        builder.headers({ "Content-Type": "application/json" });
        builder.jsonBody(normDocumentExample);
      })
      .executeTest(async (mockserver) => {
        return await getNormDocument(mockserver, buildPath(expressionEli)).then(
          (response) => {
            expect(response.data).to.deep.eq(normDocumentExample);
          },
        );
      });
  });
});
