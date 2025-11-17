import { mountSuspended } from "@nuxt/test-utils/runtime";
import type { DOMWrapper } from "@vue/test-utils";
import { expect, it, describe, beforeEach, vi } from "vitest";
import { ref } from "vue";
import type { TranslationData } from "~/composables/useTranslationDetailData";
import TranslationDetailPage from "~/pages/translations/[id]/index.vue";

const mockTranslationData: TranslationData = {
  content: {
    "@id": "AbC",
    name: "Act A",
    inLanguage: "en",
    translator: "Translation provided by someone.",
    translationOfWork: "Gesetz A",
    about: "Version information: The translation includes text",
    "ris:filename": "englisch_abc.html",
  },
  html: `<html><body>
    <p style="text-align: center; font-weight: bold">Chapter 1</p>
  </body></html>`,
};

const mockGermanOriginal = {
  item: {
    legislationIdentifier: "eli/bund/bgbl-1/1964/s902/2009-02-05/19/deu",
  },
};

vi.mock("~/composables/useTranslationData", () => ({
  fetchTranslationAndHTML: vi.fn(() => ({ data: ref(mockTranslationData) })),
  getGermanOriginal: vi.fn(() => ({ data: ref(mockGermanOriginal) })),
}));

describe("TranslationDetailPage", () => {
  let pageWrapper: ReturnType<typeof mountSuspended>;

  beforeEach(async () => {
    pageWrapper = await mountSuspended(TranslationDetailPage);
  });

  describe("Page rendering", () => {
    it("displays the title of the translation", () => {
      const pageHeader = pageWrapper.find("h1");
      expect(pageHeader.exists()).toBe(true);
      expect(pageHeader.text()).toContain("Act A");
    });

    it("renders the translation content (HTML body)", () => {
      const contentParagraphs: DOMWrapper<Element>[] = pageWrapper.findAll("p");

      const chapterParagraph = contentParagraphs.find(
        (p: DOMWrapper<Element>) => p.text() === "Chapter 1",
      );

      expect(chapterParagraph?.exists()).toBe(true);
    });
  });

  describe("Alert section", () => {
    it("contains a link to the German original", () => {
      const alertSections = pageWrapper.findAll("div[role='alert']");
      expect(alertSections).toHaveLength(1);

      const firstAlert = alertSections[0];
      const germanOriginalLink = firstAlert.findComponent({ name: "NuxtLink" });

      expect(germanOriginalLink.exists()).toBe(true);
      expect(germanOriginalLink.props("to")).toContain(
        mockGermanOriginal.item.legislationIdentifier,
      );
    });
  });

  describe("Details tab", () => {
    it("shows translator and version info after opening Details tab", async () => {
      const detailsTabLink = pageWrapper.get("a[href='#details']");
      await detailsTabLink.trigger("click");

      const detailsListItems = pageWrapper.findAll("dd");
      expect(detailsListItems).toHaveLength(2);

      expect(detailsListItems[0].text()).toBe("someone.");
      expect(detailsListItems[1].text()).toBe("The translation includes text");
    });
  });
});
