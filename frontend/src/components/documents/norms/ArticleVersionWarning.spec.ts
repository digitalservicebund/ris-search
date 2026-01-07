import { render, screen } from "@testing-library/vue";
import ArticleVersionWarning from "./ArticleVersionWarning.vue";
import type { Article } from "~/types";
import { parseDateGermanLocalTime } from "~/utils/dateFormatting";
import type { ValidityInterval } from "~/utils/norm";

vi.mock("~/utils/norm", async (importOriginal) => {
  const mod = await importOriginal<Record<string, unknown>>();
  return {
    ...mod,
    getValidityStatus: vi.fn((interval?: ValidityInterval) => {
      if (
        interval?.from === parseDateGermanLocalTime("1990-01-01") &&
        interval?.to === parseDateGermanLocalTime("2000-01-01")
      ) {
        return "Expired";
      }
      if (interval?.from === parseDateGermanLocalTime("2100-01-01"))
        return "FutureInForce";
      return "InForce";
    }),
  };
});

const VersionWarningMessageStub = {
  name: "VersionWarningMessage",
  template: '<div data-testid="version-warning-message"></div>',
};

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

describe("ArticleVersionWarning", () => {
  const inForceVersionLink =
    "/norms/eli/bund/bgbl-1/2000/s100/2000-01-01/1/deu";
  for (const currentArticle of articleTestData) {
    it(`shows warning for article with entry date ${currentArticle.entryIntoForceDate} and expiry date ${currentArticle.expiryDate}`, () => {
      render(ArticleVersionWarning, {
        props: { inForceVersionLink, currentArticle },
        global: { stubs: { VersionWarningMessage: VersionWarningMessageStub } },
      });
      expect(screen.getByTestId("version-warning-message")).toBeInTheDocument();
    });
  }
});
