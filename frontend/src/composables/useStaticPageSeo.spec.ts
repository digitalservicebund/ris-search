import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useStaticPageSeo } from "./useStaticPageSeo";
import type staticPageSeo from "~/i18n/staticPageSeo.json";

const mockUseHead = vi.fn();
mockNuxtImport("useHead", () => mockUseHead);

const mockUrl = new URL("https://example.com/test-page");
const mockUseRequestURL = vi.fn(() => mockUrl);
mockNuxtImport("useRequestURL", () => mockUseRequestURL);

type MetaTag = {
  name?: string;
  property?: string;
  content: string;
};

describe("useStaticPageSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("with valid page keys", () => {
    it("should set correct meta tags for startseite page", () => {
      useStaticPageSeo("startseite");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Rechtsinformationen des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
          },
          { property: "og:title", content: "Rechtsinformationen des Bundes" },
          {
            property: "og:description",
            content:
              "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          { name: "twitter:title", content: "Rechtsinformationen des Bundes" },
          {
            name: "twitter:description",
            content:
              "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
          },
        ],
      });
    });

    it("should set correct meta tags for suche page", () => {
      useStaticPageSeo("suche");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Suche im Rechtsinformationsportal des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Finden Sie gezielt Gesetze, Verordnungen und Entscheidungen – schnell, präzise und übersichtlich.",
          },
          {
            property: "og:title",
            content: "Suche im Rechtsinformationsportal des Bundes",
          },
          {
            property: "og:description",
            content:
              "Finden Sie gezielt Gesetze, Verordnungen und Entscheidungen – schnell, präzise und übersichtlich.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content: "Suche im Rechtsinformationsportal des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Finden Sie gezielt Gesetze, Verordnungen und Entscheidungen – schnell, präzise und übersichtlich.",
          },
        ],
      });
    });

    it("should set correct meta tags for ueber page", () => {
      useStaticPageSeo("ueber");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Über das neue Rechtsinformationsportal des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Erfahren Sie, wie das Portal Gesetze und Urteile für alle frei zugänglich macht.",
          },
          {
            property: "og:title",
            content: "Über das neue Rechtsinformationsportal des Bundes",
          },
          {
            property: "og:description",
            content:
              "Erfahren Sie, wie das Portal Gesetze und Urteile für alle frei zugänglich macht.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content: "Über das neue Rechtsinformationsportal des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Erfahren Sie, wie das Portal Gesetze und Urteile für alle frei zugänglich macht.",
          },
        ],
      });
    });

    it("should set correct meta tags for feedback page", () => {
      useStaticPageSeo("feedback");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Feedback zum Rechtsinformationsportal des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Teilen Sie Ihre Rückmeldungen und Anregungen zur Testphase – Ihr Input hilft bei der Weiterentwicklung.",
          },
          {
            property: "og:title",
            content: "Feedback zum Rechtsinformationsportal des Bundes",
          },
          {
            property: "og:description",
            content:
              "Teilen Sie Ihre Rückmeldungen und Anregungen zur Testphase – Ihr Input hilft bei der Weiterentwicklung.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content: "Feedback zum Rechtsinformationsportal des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Teilen Sie Ihre Rückmeldungen und Anregungen zur Testphase – Ihr Input hilft bei der Weiterentwicklung.",
          },
        ],
      });
    });

    it("should set correct meta tags for kontakt page", () => {
      useStaticPageSeo("kontakt");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Kontakt zum Rechtsinformationsportal des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Hier erreichen Sie uns bei Fragen, Hinweisen oder technischen Problemen rund um das Portal.",
          },
          {
            property: "og:title",
            content: "Kontakt zum Rechtsinformationsportal des Bundes",
          },
          {
            property: "og:description",
            content:
              "Hier erreichen Sie uns bei Fragen, Hinweisen oder technischen Problemen rund um das Portal.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content: "Kontakt zum Rechtsinformationsportal des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Hier erreichen Sie uns bei Fragen, Hinweisen oder technischen Problemen rund um das Portal.",
          },
        ],
      });
    });

    it("should set correct meta tags for impressum page", () => {
      useStaticPageSeo("impressum");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Impressum des Rechtsinformationsportals des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Angaben gemäß § 5 TMG – Herausgeber, Verantwortliche und rechtliche Hinweise zum Portal.",
          },
          {
            property: "og:title",
            content: "Impressum des Rechtsinformationsportals des Bundes",
          },
          {
            property: "og:description",
            content:
              "Angaben gemäß § 5 TMG – Herausgeber, Verantwortliche und rechtliche Hinweise zum Portal.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content: "Impressum des Rechtsinformationsportals des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Angaben gemäß § 5 TMG – Herausgeber, Verantwortliche und rechtliche Hinweise zum Portal.",
          },
        ],
      });
    });

    it("should set correct meta tags for datenschutz page", () => {
      useStaticPageSeo("datenschutz");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Datenschutzrichtlinie des Rechtsinformationsportals des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Wie wir Ihre Daten schützen, welche Rechte Sie haben und welche Verfahren wir anwenden.",
          },
          {
            property: "og:title",
            content:
              "Datenschutzrichtlinie des Rechtsinformationsportals des Bundes",
          },
          {
            property: "og:description",
            content:
              "Wie wir Ihre Daten schützen, welche Rechte Sie haben und welche Verfahren wir anwenden.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content:
              "Datenschutzrichtlinie des Rechtsinformationsportals des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Wie wir Ihre Daten schützen, welche Rechte Sie haben und welche Verfahren wir anwenden.",
          },
        ],
      });
    });

    it("should set correct meta tags for barrierefreiheit page", () => {
      useStaticPageSeo("barrierefreiheit");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Barrierefreiheit im Rechtsinformationsportal des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Informationen zur digitalen Zugänglichkeit, zum technischen Standard und zur Feedback-Möglichkeit.",
          },
          {
            property: "og:title",
            content: "Barrierefreiheit im Rechtsinformationsportal des Bundes",
          },
          {
            property: "og:description",
            content:
              "Informationen zur digitalen Zugänglichkeit, zum technischen Standard und zur Feedback-Möglichkeit.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content: "Barrierefreiheit im Rechtsinformationsportal des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Informationen zur digitalen Zugänglichkeit, zum technischen Standard und zur Feedback-Möglichkeit.",
          },
        ],
      });
    });

    it("should set correct meta tags for cookies page", () => {
      useStaticPageSeo("cookies");

      expect(mockUseHead).toHaveBeenCalledWith({
        title:
          "Cookie-Einstellungen für das Rechtsinformationsportal des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Wählen Sie, welche Cookies Sie zulassen – für eine bessere, datenschutzgerechte Nutzung.",
          },
          {
            property: "og:title",
            content:
              "Cookie-Einstellungen für das Rechtsinformationsportal des Bundes",
          },
          {
            property: "og:description",
            content:
              "Wählen Sie, welche Cookies Sie zulassen – für eine bessere, datenschutzgerechte Nutzung.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content:
              "Cookie-Einstellungen für das Rechtsinformationsportal des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Wählen Sie, welche Cookies Sie zulassen – für eine bessere, datenschutzgerechte Nutzung.",
          },
        ],
      });
    });

    it("should set correct meta tags for opensource page", () => {
      useStaticPageSeo("opensource");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Open Source im Rechtsinformationsportal des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Informationen zur verwendeten Open-Source-Software, zu Lizenzen und Beteiligungsmöglichkeiten.",
          },
          {
            property: "og:title",
            content: "Open Source im Rechtsinformationsportal des Bundes",
          },
          {
            property: "og:description",
            content:
              "Informationen zur verwendeten Open-Source-Software, zu Lizenzen und Beteiligungsmöglichkeiten.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content: "Open Source im Rechtsinformationsportal des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Informationen zur verwendeten Open-Source-Software, zu Lizenzen und Beteiligungsmöglichkeiten.",
          },
        ],
      });
    });

    it("should set correct meta tags for nutzungstests page", () => {
      useStaticPageSeo("nutzungstests");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "Nutzungstests zum Rechtsinformationsportal des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Erfahren Sie, wie das Portal getestet wird, welche Ergebnisse vorliegen und wie Sie teilnehmen können.",
          },
          {
            property: "og:title",
            content: "Nutzungstests zum Rechtsinformationsportal des Bundes",
          },
          {
            property: "og:description",
            content:
              "Erfahren Sie, wie das Portal getestet wird, welche Ergebnisse vorliegen und wie Sie teilnehmen können.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content: "Nutzungstests zum Rechtsinformationsportal des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Erfahren Sie, wie das Portal getestet wird, welche Ergebnisse vorliegen und wie Sie teilnehmen können.",
          },
        ],
      });
    });

    it("should set correct meta tags for api page", () => {
      useStaticPageSeo("api");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "API-Dokumentation des Rechtsinformationsportals des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
          },
          {
            property: "og:title",
            content:
              "API-Dokumentation des Rechtsinformationsportals des Bundes",
          },
          {
            property: "og:description",
            content:
              "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content:
              "API-Dokumentation des Rechtsinformationsportals des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Technische Dokumentation und API-Referenz für den Zugriff auf Rechtsinformationen des Bundes.",
          },
        ],
      });
    });

    it("should set correct meta tags for nutzungstests-datenschutz page", () => {
      useStaticPageSeo("nutzungstests-datenschutz");

      expect(mockUseHead).toHaveBeenCalledWith({
        title:
          "Datenschutzerklärung zu den Nutzungstests des Rechtsinformationsportals des Bundes",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Erfahren Sie, wie wir Ihre Daten bei Teilnahme an unseren Nutzungstests erfassen, verwenden und schützen",
          },
          {
            property: "og:title",
            content:
              "Datenschutzerklärung zu den Nutzungstests des Rechtsinformationsportals des Bundes",
          },
          {
            property: "og:description",
            content:
              "Erfahren Sie, wie wir Ihre Daten bei Teilnahme an unseren Nutzungstests erfassen, verwenden und schützen",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content:
              "Datenschutzerklärung zu den Nutzungstests des Rechtsinformationsportals des Bundes",
          },
          {
            name: "twitter:description",
            content:
              "Erfahren Sie, wie wir Ihre Daten bei Teilnahme an unseren Nutzungstests erfassen, verwenden und schützen",
          },
        ],
      });
    });

    it("should set correct meta tags for translations-list page", () => {
      useStaticPageSeo("translations-list");

      expect(mockUseHead).toHaveBeenCalledWith({
        title: "English Translations of German Federal Laws and Regulations",
        link: [{ rel: "canonical", href: "https://example.com/test-page" }],
        meta: [
          {
            name: "description",
            content:
              "Access official English translations of selected German laws and regulations. These translations are for informational purposes only and are not legally binding.",
          },
          {
            property: "og:title",
            content:
              "English Translations of German Federal Laws and Regulations",
          },
          {
            property: "og:description",
            content:
              "Access official English translations of selected German laws and regulations. These translations are for informational purposes only and are not legally binding.",
          },
          { property: "og:url", content: "https://example.com/test-page" },
          {
            name: "twitter:title",
            content:
              "English Translations of German Federal Laws and Regulations",
          },
          {
            name: "twitter:description",
            content:
              "Access official English translations of selected German laws and regulations. These translations are for informational purposes only and are not legally binding.",
          },
        ],
      });
    });
  });

  describe("with invalid page keys", () => {
    it("should return early and not call useHead when page key does not exist", () => {
      // @ts-expect-error - Testing invalid key
      useStaticPageSeo("nonexistent-page");

      expect(mockUseHead).not.toHaveBeenCalled();
    });

    it("should return early and not call useHead when page key is undefined", () => {
      // @ts-expect-error - Testing undefined key
      useStaticPageSeo(undefined);

      expect(mockUseHead).not.toHaveBeenCalled();
    });

    it("should return early and not call useHead when page key is null", () => {
      // @ts-expect-error - Testing null key
      useStaticPageSeo(null);

      expect(mockUseHead).not.toHaveBeenCalled();
    });

    it("should return early and not call useHead when page key is empty string", () => {
      // @ts-expect-error - Testing empty string key
      useStaticPageSeo("");

      expect(mockUseHead).not.toHaveBeenCalled();
    });
  });

  describe("URL handling", () => {
    it("should use the URL from useRequestURL for canonical link and og:url", () => {
      const customUrl = new URL("https://custom-domain.com/custom-path");
      mockUseRequestURL.mockReturnValueOnce(customUrl);

      useStaticPageSeo("startseite");

      expect(mockUseRequestURL).toHaveBeenCalled();
      expect(mockUseHead).toHaveBeenCalledWith(
        expect.objectContaining({
          link: [
            { rel: "canonical", href: "https://custom-domain.com/custom-path" },
          ],
          meta: expect.arrayContaining([
            {
              property: "og:url",
              content: "https://custom-domain.com/custom-path",
            },
          ]),
        }),
      );
    });

    it("should handle different URL formats correctly", () => {
      const urlWithQuery = new URL(
        "https://example.com/page?param=value&other=test",
      );
      mockUseRequestURL.mockReturnValueOnce(urlWithQuery);

      useStaticPageSeo("startseite");

      expect(mockUseHead).toHaveBeenCalledWith(
        expect.objectContaining({
          link: [
            {
              rel: "canonical",
              href: "https://example.com/page?param=value&other=test",
            },
          ],
          meta: expect.arrayContaining([
            {
              property: "og:url",
              content: "https://example.com/page?param=value&other=test",
            },
          ]),
        }),
      );
    });
  });

  describe("meta tag structure", () => {
    it("should include all required meta tags", () => {
      useStaticPageSeo("startseite");

      const callArgs = mockUseHead.mock.calls[0][0];

      expect(callArgs).toHaveProperty("title");
      expect(callArgs).toHaveProperty("link");
      expect(callArgs).toHaveProperty("meta");

      expect(callArgs.link).toHaveLength(1);
      expect(callArgs.link[0]).toEqual({
        rel: "canonical",
        href: "https://example.com/test-page",
      });

      expect(callArgs.meta).toHaveLength(6);

      // Check for description meta tag
      expect(callArgs.meta).toContainEqual({
        name: "description",
        content:
          "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
      });

      // Check for Open Graph meta tags
      expect(callArgs.meta).toContainEqual({
        property: "og:title",
        content: "Rechtsinformationen des Bundes",
      });
      expect(callArgs.meta).toContainEqual({
        property: "og:description",
        content:
          "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
      });
      expect(callArgs.meta).toContainEqual({
        property: "og:url",
        content: "https://example.com/test-page",
      });

      // Check for Twitter meta tags
      expect(callArgs.meta).toContainEqual({
        name: "twitter:title",
        content: "Rechtsinformationen des Bundes",
      });
      expect(callArgs.meta).toContainEqual({
        name: "twitter:description",
        content:
          "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
      });
    });

    it("should use the same title for page title, og:title, and twitter:title", () => {
      useStaticPageSeo("startseite");

      const callArgs = mockUseHead.mock.calls[0][0];
      const expectedTitle = "Rechtsinformationen des Bundes";

      expect(callArgs.title).toBe(expectedTitle);

      const ogTitleMeta = callArgs.meta.find(
        (meta: MetaTag) => meta.property === "og:title",
      );
      const twitterTitleMeta = callArgs.meta.find(
        (meta: MetaTag) => meta.name === "twitter:title",
      );

      expect(ogTitleMeta?.content).toBe(expectedTitle);
      expect(twitterTitleMeta?.content).toBe(expectedTitle);
    });

    it("should use the same description for meta description, og:description, and twitter:description", () => {
      useStaticPageSeo("startseite");

      const callArgs = mockUseHead.mock.calls[0][0];
      const expectedDescription =
        "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.";

      const descriptionMeta = callArgs.meta.find(
        (meta: MetaTag) => meta.name === "description",
      );
      const ogDescriptionMeta = callArgs.meta.find(
        (meta: MetaTag) => meta.property === "og:description",
      );
      const twitterDescriptionMeta = callArgs.meta.find(
        (meta: MetaTag) => meta.name === "twitter:description",
      );

      expect(descriptionMeta?.content).toBe(expectedDescription);
      expect(ogDescriptionMeta?.content).toBe(expectedDescription);
      expect(twitterDescriptionMeta?.content).toBe(expectedDescription);
    });
  });

  describe("type safety", () => {
    it("should accept all valid page keys from the JSON file", () => {
      const validKeys: (keyof typeof staticPageSeo)[] = [
        "startseite",
        "suche",
        "ueber",
        "feedback",
        "kontakt",
        "impressum",
        "datenschutz",
        "barrierefreiheit",
        "cookies",
        "opensource",
        "nutzungstests",
        "api",
        "nutzungstests-datenschutz",
        "translations-list",
      ];

      validKeys.forEach((key) => {
        expect(() => useStaticPageSeo(key)).not.toThrow();
      });
    });
  });
});
