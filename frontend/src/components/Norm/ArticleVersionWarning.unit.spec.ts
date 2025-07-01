import { mount } from "@vue/test-utils";
import ArticleVersionWarning from "./ArticleVersionWarning.vue";
import type { Article } from "~/types";

vi.mock("~/utils/normUtils", async (importOriginal) => {
  const mod = await importOriginal<Record<string, unknown>>();
  return {
    ...mod,
    getValidityStatus: vi.fn((interval?: ValidityInterval) => {
      if (
        interval?.from === parseDateGermanLocalTime("1990-01-01") &&
        interval?.to === parseDateGermanLocalTime("2000-01-01")
      ) {
        return ValidityStatus.Historical;
      }
      if (interval?.from === parseDateGermanLocalTime("2100-01-01"))
        return ValidityStatus.Future;
      return ValidityStatus.InForce;
    }),
  };
});

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
        global: { stubs: ["VersionWarningMessage"] },
      });
      expect(
        wrapper.findComponent({ name: "VersionWarningMessage" }).exists(),
      ).toBe(true);
    });
  }
});
