import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import type { Article, ArtikelFassung } from "~/types/api";
import ArticleVersionWarning from "./ArticleVersionWarning.vue";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({ query: {} as Record<string, string> })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

const workEli = "eli/bund/bgbl-1/2020/s1234";
const expression = (pointInTime: string) => `${workEli}/${pointInTime}/1/deu`;
const partOf = (...pointsInTime: string[]) =>
  pointsInTime.map((pointInTime) => ({
    "@id": `/v1/legislation/${expression(pointInTime)}`,
  }));

const historicFassung = {
  "@id": "historic",
  eId: "art-z1",
  name: "§ 1",
  temporalCoverage: "2020-01-01/2020-12-31",
  isPartOf: partOf("2020-01-01"),
} as ArtikelFassung;

const inForceFassung = {
  "@id": "in-force",
  eId: "art-z1",
  name: "§ 1",
  temporalCoverage: "2021-01-01/2029-12-31",
  isPartOf: partOf("2021-01-01", "2022-01-01", "2023-01-01"),
} as ArtikelFassung;

const futureFassung = {
  "@id": "future",
  eId: "art-z1",
  name: "§ 1",
  temporalCoverage: "2030-01-01/2034-12-31",
  isPartOf: partOf("2030-01-01", "2031-01-01"),
} as ArtikelFassung;

const laterFutureFassung = {
  "@id": "later-future",
  eId: "art-z1",
  name: "§ 1",
  temporalCoverage: "2035-01-01/..",
  isPartOf: partOf("2035-01-01"),
} as ArtikelFassung;

const allFassungen = [
  laterFutureFassung,
  futureFassung,
  inForceFassung,
  historicFassung,
];

const articleFor = (fassung: ArtikelFassung) =>
  ({ temporalCoverage: fassung.temporalCoverage }) as Article;

const linkStub = {
  NuxtLink: {
    template: '<a :href="to.path" :data-from="to.query?.from"><slot /></a>',
    props: ["to"],
  },
};

describe("ArticleVersionWarning", () => {
  beforeEach(() => {
    vi.useFakeTimers({ toFake: ["Date"] });
    vi.setSystemTime(new Date("2025-06-01T12:00:00Z"));
    useRouteMock.mockReturnValue({ query: {} });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("links a historic Fassung to the valid Fassung in the in force expression", async () => {
    await renderSuspended(ArticleVersionWarning, {
      props: {
        currentArticle: articleFor(historicFassung),
        fassungen: allFassungen,
        inForceExpressionEli: expression("2022-01-01"),
      },
      global: { stubs: linkStub },
    });

    const link = screen.getByRole("link", {
      name: "Zur aktuell gültigen Fassung",
      description: "Sie lesen eine historische Fassung.",
    });
    expect(link).toHaveAttribute(
      "href",
      `/gesetze/${expression("2022-01-01")}/art-z1`,
    );
  });

  it("falls back to the newest expression of the valid Fassung", async () => {
    await renderSuspended(ArticleVersionWarning, {
      props: {
        currentArticle: articleFor(historicFassung),
        fassungen: allFassungen,
      },
      global: { stubs: linkStub },
    });

    expect(
      screen.getByRole("link", { name: "Zur aktuell gültigen Fassung" }),
    ).toHaveAttribute("href", `/gesetze/${expression("2023-01-01")}/art-z1`);
  });

  it("shows a historic Fassung without link if there is no valid Fassung", async () => {
    await renderSuspended(ArticleVersionWarning, {
      props: {
        currentArticle: articleFor(historicFassung),
        fassungen: [historicFassung, futureFassung],
        inForceExpressionEli: expression("2022-01-01"),
      },
    });

    expect(
      screen.getByText("Sie lesen eine historische Fassung."),
    ).toBeInTheDocument();
    expect(screen.queryByRole("link")).not.toBeInTheDocument();
  });

  it("links a valid Fassung to the next future Fassung in its newest expression", async () => {
    await renderSuspended(ArticleVersionWarning, {
      props: {
        currentArticle: articleFor(inForceFassung),
        fassungen: allFassungen,
        inForceExpressionEli: expression("2022-01-01"),
      },
      global: { stubs: linkStub },
    });

    const link = screen.getByRole("link", {
      name: "Zur zukünftigen Fassung",
      description: "Ab 01.01.2030 gilt eine neue Fassung.",
    });
    expect(link).toHaveAttribute(
      "href",
      `/gesetze/${expression("2031-01-01")}/art-z1`,
    );
  });

  it("shows no message for a valid Fassung without future Fassung", async () => {
    await renderSuspended(ArticleVersionWarning, {
      props: {
        currentArticle: articleFor(inForceFassung),
        fassungen: [historicFassung, inForceFassung],
        inForceExpressionEli: expression("2022-01-01"),
      },
    });

    expect(screen.queryByText(/Fassung/)).not.toBeInTheDocument();
  });

  it("links a future Fassung to the valid Fassung", async () => {
    await renderSuspended(ArticleVersionWarning, {
      props: {
        currentArticle: articleFor(futureFassung),
        fassungen: allFassungen,
        inForceExpressionEli: expression("2022-01-01"),
      },
      global: { stubs: linkStub },
    });

    const link = screen.getByRole("link", {
      name: "Zur aktuell gültigen Fassung",
      description: "Sie lesen eine zukünftige Fassung.",
    });
    expect(link).toHaveAttribute(
      "href",
      `/gesetze/${expression("2022-01-01")}/art-z1`,
    );
  });

  it("shows a future Fassung without link if there is no valid Fassung", async () => {
    await renderSuspended(ArticleVersionWarning, {
      props: {
        currentArticle: articleFor(futureFassung),
        fassungen: [futureFassung, laterFutureFassung],
      },
    });

    expect(
      screen.getByText("Sie lesen eine zukünftige Fassung."),
    ).toBeInTheDocument();
    expect(screen.queryByRole("link")).not.toBeInTheDocument();
  });

  it("keeps the from query parameter in links", async () => {
    useRouteMock.mockReturnValue({ query: { from: "/suche?q=test" } });

    await renderSuspended(ArticleVersionWarning, {
      props: {
        currentArticle: articleFor(historicFassung),
        fassungen: allFassungen,
        inForceExpressionEli: expression("2022-01-01"),
      },
      global: { stubs: linkStub },
    });

    expect(
      screen.getByRole("link", { name: "Zur aktuell gültigen Fassung" }),
    ).toHaveAttribute("data-from", "/suche?q=test");
  });
});
