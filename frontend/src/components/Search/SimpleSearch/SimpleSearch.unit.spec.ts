import { vi } from "vitest";
import { mountSuspended } from "@nuxt/test-utils/runtime";
import SimpleSearch from "./SimpleSearch.vue";
import { useSimpleSearchParamsStore } from "~/stores/searchParams";
import { DocumentKind } from "~/types";

vi.mock("@/services/searchService", () => {
  return { search: () => ({}) };
});

interface SimpleSearchVM extends ComponentPublicInstance {
  title: string;
}

describe("SimpleSearch", () => {
  it("sets the title attribute", async () => {
    const wrapper = await mountSuspended(SimpleSearch);
    const store = useSimpleSearchParamsStore();
    await nextTick();
    expect((wrapper.vm as SimpleSearchVM).title).toBe("Suche");

    store.category = DocumentKind.CaseLaw;
    await nextTick();
    expect((wrapper.vm as SimpleSearchVM).title).toBe("Rechtsprechung — Suche");

    store.query = "frühstück brötchen";
    await nextTick();
    expect((wrapper.vm as SimpleSearchVM).title).toBe(
      "frühstück brötchen — Suche",
    );
  });
});
