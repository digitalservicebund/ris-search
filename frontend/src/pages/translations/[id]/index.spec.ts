import { mountSuspended } from "@nuxt/test-utils/runtime";
import { expect, it, describe, vi } from "vitest";
import { ref } from "vue";
import type { TranslationData } from "~/composables/useTranslationData";
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
  html: '<html><body><p style="text-align: center; font-weight: bold">Chapter 1</p></body></html>',
};

vi.mock("~/composables/useTranslationData", () => ({
  fetchTranslationAndHTML: vi.fn(() => ({
    data: ref(mockTranslationData),
  })),
}));

describe("translations detail page", async () => {
  it("displays translation detail page correctly", async () => {
    const wrapper = await mountSuspended(TranslationDetailPage);

    const pageHeader = wrapper.find("h1");
    expect(pageHeader.exists()).toBe(true);
    expect(pageHeader.text()).toContain("Act A");

    const allParagraphs = wrapper.findAll("p");
    const actAParagraph = allParagraphs.find((p) => p.text() === "Chapter 1");
    expect(actAParagraph?.exists()).toBe(true);

    const tabButton = wrapper.get("button[aria-label*='Details']");
    await tabButton.trigger("click");

    const metaData = wrapper.findAll("dd");
    expect(metaData.length).toBe(2);

    expect(metaData[0].text()).toBe("someone.");
    expect(metaData[1].text()).toBe("The translation includes text");
  });
});
