import { mount } from "@vue/test-utils";
import ArticleVersionWarning from "./ArticleVersionWarning.vue";
import type { Article } from "~/types";

vi.mock("~/composables/useNormVersions", () => ({
  getStatusLabel: vi.fn((entry, expiry) => {
    if (entry === "past" && expiry === "past") return "historical";
    if (entry === "future") return "future";
    return "inForce";
  }),
}));
const formattedDate = (d: string | null) => {
  if (!d) return null;
  if (d === "1990-01-01" || d === "2000-01-01") return "past";
  if (d === "2100-01-01") return "future";
  return "now";
};
vi.stubGlobal("formattedDate", formattedDate);

const articleTestData = [
  {
    entryIntoForceDate: "1990-01-01",
    expiryDate: "2000-01-01",
  },
  {
    entryIntoForceDate: "2100-01-01",
    expiryDate: null,
  },
  {
    entryIntoForceDate: "2000-01-02",
    expiryDate: null,
  },
] as unknown as Article[];

describe("ArticleVersionWarning.vue", () => {
  const inForceVersionLink =
    "/norms/eli/bund/bgbl-1/2000/s100/2000-01-01/1/deu";
  for (const currentArticle of articleTestData) {
    it(`shows warning for article with entry date ${currentArticle.entryIntoForceDate} and expiry date ${currentArticle.expiryDate}`, () => {
      const wrapper = mount(ArticleVersionWarning, {
        props: { inForceVersionLink, currentArticle },
        global: { stubs: ["WarningMessage"] },
      });
      expect(wrapper.findComponent({ name: "WarningMessage" }).exists()).toBe(
        true,
      );
    });
  }
});
