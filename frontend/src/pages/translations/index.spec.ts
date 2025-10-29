import { mountSuspended } from "@nuxt/test-utils/runtime";
import { expect, it, describe, vi } from "vitest";
import { ref } from "vue";
import type { TranslationContent } from "~/composables/useTranslationData";
import TranslationListPage from "~/pages/translations/index.vue";

const mockTranslationData: TranslationContent[] = [
  {
    "@id": "Cde",
    name: "Act B",
    inLanguage: "en",
    translator: "…",
    translationOfWork: "Gesetz B",
    about: "…",
    "ris:filename": "englisch_cde.html",
  },
  {
    "@id": "AbC",
    name: "Act A",
    inLanguage: "en",
    translator: "…",
    translationOfWork: "Gesetz A",
    about: "…",
    "ris:filename": "englisch_abc.html",
  },
];

vi.mock("~/composables/useTranslationData", () => ({
  fetchTranslationList: vi.fn(() => ({
    data: ref(mockTranslationData),
  })),
}));

describe("translations list page", async () => {
  it("displays translation list correctly", async () => {
    const wrapper = await mountSuspended(TranslationListPage);

    const pageHeader = wrapper.find("h1");
    expect(pageHeader.exists()).toBe(true);
    expect(pageHeader.text()).toContain("English Translations");

    const translationListElements = wrapper.findAll("h2");
    expect(translationListElements.length).toBe(2);
    expect(translationListElements[0]?.text()).toBe("Act A");
    expect(translationListElements[1]?.text()).toBe("Act B");

    const firstTranslationLink = wrapper.find("a[href='translations/AbC']");
    expect(firstTranslationLink.exists()).toBe(true);
    expect(firstTranslationLink.text()).toContain("Act A");
  });

  it("list can be filteres", async () => {
    const wrapper = await mountSuspended(TranslationListPage);

    const searchInput = wrapper.find("input[type='search']");
    await searchInput.setValue("Gesetz B");
    const form = wrapper.find("form");
    await form.trigger("submit");

    const translationListElements = wrapper.findAll("h2");
    expect(translationListElements.length).toBe(2);
    expect(translationListElements[0]?.text()).toBe("Act B");
  });
});
