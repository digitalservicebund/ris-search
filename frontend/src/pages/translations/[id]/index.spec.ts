import { mountSuspended } from "@nuxt/test-utils/runtime";
import { expect, it, describe, vi } from "vitest";
import { ref } from "vue";
import TranslationDetailPage from "~/pages/translations/[id]/index.vue";
import type { TranslationData } from "~/pages/translations/useTranslationData";

describe("translations detail page", async () => {
  it("displays translation detail page correctly and replaces prefixes", async () => {
    const mockTranslationData = vi.hoisted<TranslationData>(() => ({
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
    }));

    vi.mock("~/pages/translations/useTranslationData", () => ({
      fetchTranslationAndHTML: vi.fn(() => ({
        data: ref(mockTranslationData),
      })),
    }));

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

describe("translations detail page", async () => {
  it("displays translation detail page correctly and does not replaces prefixes", async () => {
    const mockTranslationData1 = vi.hoisted<TranslationData>(() => ({
      content: {
        "@id": "AbC",
        name: "Act A",
        inLanguage: "en",
        translator: "someone.",
        translationOfWork: "Gesetz A",
        about: "The translation includes text",
        "ris:filename": "englisch_abc.html",
      },
      html: '<html><body><p style="text-align: center; font-weight: bold">Chapter 1</p></body></html>',
    }));

    vi.mock("~/pages/translations/useTranslationData", () => ({
      fetchTranslationAndHTML: vi.fn(() => ({
        data: ref(mockTranslationData1),
      })),
    }));

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
