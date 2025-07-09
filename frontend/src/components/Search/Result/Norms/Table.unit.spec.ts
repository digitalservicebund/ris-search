// @ts-nocheck
import { mountSuspended } from "@nuxt/test-utils/runtime";
import { RouterLinkStub } from "@vue/test-utils";
import { describe, expect, it } from "vitest";
import Table from "./Table.vue";
import type { LegislationWork, SearchResult } from "~/types";

const searchResult: SearchResult<LegislationWork> = {
  item: {
    "@type": "Legislation",
    "@id": "id",
    name: "Sample Norm",
    legislationIdentifier: "eli/work-LEG12345",
    alternateName: "alternateName",
    abbreviation: "abbreviation",
    legislationDate: "2024-10-05",
    isPartOf: {
      name: "The Official Gazette",
    },
    workExample: {
      "@id": "id/expression",
      "@type": "Legislation",
      hasPart: [],
      legislationIdentifier: "eli/work-LEG12345/expression-LEG12345",
      encoding: [],
      tableOfContents: [],
    },
  },
  textMatches: [],
};

describe("Table.vue", () => {
  it("renders correctly with Norm item for norms view", async () => {
    const wrapper = await mountSuspended(Table, {
      props: {
        searchResults: [searchResult],
      },
      stubs: {
        NuxtLink: RouterLinkStub,
      },
    });

    const resultText = wrapper.text();
    expect(wrapper.find('[data-testid="result-row"]').exists()).toBe(true);
    expect(wrapper.html()).toContain(
      `<a href="/norms/${searchResult.item.workExample.legislationIdentifier}/regelungstext">${searchResult.item.name}</a>`,
    );
    expect(resultText).toContain("N");
    expect(resultText).toContain("05.10.2024");
  });

  it('renders "-" if no date is available for a norms view', async () => {
    searchResult.item.legislationDate = "";
    const wrapper = await mountSuspended(Table, {
      props: {
        searchResults: [searchResult],
      },
      stubs: {
        NuxtLink: RouterLinkStub,
      },
    });
    const result = wrapper.find('[data-testid="result-row"]');
    expect(result.exists()).toBe(true);
    expect(result.text()).toContain("-");
  });
});
