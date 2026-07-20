import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import type { Article } from "~/types/api";
import type { ValidityInterval } from "~/utils/norm";
import ArticleVersionWarning from "./ArticleVersionWarning.vue";

mockNuxtImport("getValidityStatus", () => {
  return vi.fn((interval?: ValidityInterval) => {
    if (interval?.from?.year() === 1990) return "Expired";
    if (interval?.from?.year() === 2100) return "FutureInForce";
    return "InForce";
  });
});

const articles = [
  { temporalCoverage: "1990-01-01/2000-01-01" },
  { temporalCoverage: "2100-01-01/.." },
] as unknown as Article[];

describe("ArticleVersionWarning", () => {
  const inForceVersionLink =
    "/gesetze/eli/bund/bgbl-1/2000/s100/2000-01-01/1/deu";

  it.each(articles)("shows warning for article %s", async (currentArticle) => {
    await renderSuspended(ArticleVersionWarning, {
      props: { inForceVersionLink, currentArticle },
    });

    expect(screen.getByRole("alert")).toBeInTheDocument();
  });
});
