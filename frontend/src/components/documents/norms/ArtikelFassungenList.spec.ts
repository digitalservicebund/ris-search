import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, within } from "@testing-library/vue";
import type { FetchHook } from "ofetch";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import type { ArtikelFassung, LegislationExpression } from "~/types/api.ts";
import ArtikelFassungenList from "./ArtikelFassungenList.vue";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

function createFassung(
  identifier: string,
  temporalCoverage: string,
): ArtikelFassung {
  return {
    "@id": identifier,
    eId: "art-1",
    name: "§ 1",
    temporalCoverage,
    revision: identifier,
    encoding: [
      {
        "@id": "",
        contentUrl: `/v1/legislation/${identifier}.html`,
        encodingFormat: "text/html",
        inLanguage: "deu",
      },
    ],
  };
}

const currentLegislationIdentifier =
  "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1";

const futureFassung = createFassung(
  "eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu#art-1",
  "2031-01-01/..",
);
const currentFassung = createFassung(
  "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu#art-1",
  "2020-01-01/2030-12-31",
);
const pastFassung = createFassung(
  "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu#art-1",
  "2000-01-05/2019-12-31",
);

function createGesamtausgabe(
  legislationIdentifier: string,
  temporalCoverage: string,
) {
  return {
    "@id": `/v1/legislation/${legislationIdentifier}`,
    legislationIdentifier,
    temporalCoverage,
  } as LegislationExpression;
}

const gesamtausgabe = createGesamtausgabe(
  "eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu/regelungstext-1",
  "2031-01-01/..",
);

function mockBackend({
  html = "<p>Norm content</p>",
  gesamtausgaben = [gesamtausgabe],
}: { html?: string; gesamtausgaben?: LegislationExpression[] } = {}) {
  mockFetch.mockImplementation(async (url: string) =>
    url.endsWith("/legislations")
      ? { member: gesamtausgaben }
      : `<html><body>${html}</body></html>`,
  );
}

function props(
  fassungen: ArtikelFassung[] = [pastFassung, currentFassung, futureFassung],
) {
  return {
    currentLegislationIdentifier,
    currentFassungId: currentFassung.revision!,
    fassungen,
  };
}

describe("ArtikelFassungenList", () => {
  beforeEach(() => {
    mockFetch.mockReset();
    // jsdom doesn't implement scrolling
    Element.prototype.scrollIntoView = vi.fn();
  });

  afterEach(() => {
    // @ts-expect-error restore jsdom's state, which has no implementation
    delete Element.prototype.scrollIntoView;
  });

  it("lists fassungen, sorted by date, newest first", async () => {
    await renderSuspended(ArtikelFassungenList, { props: props() });

    const rows = screen.getAllByRole("listitem");
    expect(rows).toHaveLength(3);

    expect(within(rows[0]!).getByText("01.01.2031")).toBeInTheDocument();

    expect(within(rows[1]!).getByText("01.01.2020")).toBeInTheDocument();
    expect(within(rows[1]!).getByText("31.12.2030")).toBeInTheDocument();

    expect(within(rows[2]!).getByText("05.01.2000")).toBeInTheDocument();
    expect(within(rows[2]!).getByText("31.12.2019")).toBeInTheDocument();
  });

  it("joins the dates with a dash when both exist", async () => {
    await renderSuspended(ArtikelFassungenList, {
      props: props([pastFassung, currentFassung]),
    });

    for (const row of screen.getAllByRole("listitem")) {
      expect(within(row).getByText("–")).toHaveAttribute("aria-hidden", "true");
    }
  });

  it.each([
    ["from", "../2019-12-31"],
    ["to", "2031-01-01/.."],
  ])(
    "shows no placeholder and no dash for a missing %s date on desktop",
    async (_, temporalCoverage) => {
      await renderSuspended(ArtikelFassungenList, {
        props: props([createFassung("eli/bund/test#art-1", temporalCoverage)]),
      });

      const row = screen.getByRole("listitem");
      expect(within(row).getByText("—")).toHaveClass("md:hidden");
      expect(within(row).queryByText("–")).not.toBeInTheDocument();
    },
  );

  it("shows the column labels as a header, but keeps it from SR", async () => {
    await renderSuspended(ArtikelFassungenList, { props: props() });

    expect(screen.getByText("Gültig ab")).toBeInTheDocument();
    expect(screen.getByText("Gültig bis")).toBeInTheDocument();

    // Only the 3 fassung rows are exposed to screen readers, not the header
    expect(screen.getAllByRole("listitem")).toHaveLength(3);
  });

  it("marks the current fassung as the current entry", async () => {
    await renderSuspended(ArtikelFassungenList, { props: props() });

    const currentRow = screen.getByRole("listitem", { current: true });
    expect(currentRow).toHaveTextContent("01.01.2020");
    expect(currentRow).toHaveTextContent("31.12.2030");
  });

  it("shows only the Gesamtausgaben, not the content, of the current fassung", async () => {
    mockBackend();
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2020"));

    expect(
      await screen.findByRole("link", { name: /^Gesamtausgabe/ }),
    ).toBeVisible();
    expect(screen.queryByText("Norm content")).not.toBeInTheDocument();
  });

  it("opens the Gesamtausgaben selection of the current fassung right away", async () => {
    mockBackend({
      gesamtausgaben: [
        gesamtausgabe,
        createGesamtausgabe(currentLegislationIdentifier, "2020-01-01/.."),
      ],
    });
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2020"));

    expect(screen.getByRole("list", { name: "Gesamtausgaben" })).toBeVisible();
  });

  it("treats no fassung as current when none has the current revision", async () => {
    mockBackend();
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, {
      props: { ...props(), currentFassungId: "eli/bund/other#art-1" },
    });

    expect(
      screen.queryByRole("listitem", { current: true }),
    ).not.toBeInTheDocument();

    await user.click(screen.getByText("01.01.2020"));

    expect(await screen.findByText("Norm content")).toBeVisible();
  });

  it("renders the other fassungen as a closed accordion by default", async () => {
    await renderSuspended(ArtikelFassungenList, { props: props() });

    const rows = screen.getAllByRole("listitem");
    const futureFassungRow = rows[0]!;
    const pastFassungRow = rows[2]!;

    expect(
      within(futureFassungRow).queryByRole("status"),
    ).not.toBeInTheDocument();
    expect(
      within(pastFassungRow).queryByRole("status"),
    ).not.toBeInTheDocument();
  });

  it("shows a loading spinner while the content is being fetched", async () => {
    mockFetch.mockReturnValue(new Promise(() => {}));
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });
    const futureFassungRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureFassungRow).getByText("01.01.2031"));

    expect(within(futureFassungRow).getByLabelText("Ladestatus")).toBeVisible();
  });

  it("loads the HTML and Gesamtausgaben of the expanded fassung", async () => {
    mockBackend();
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2031"));

    expect(mockFetch).toHaveBeenCalledWith(
      "/v1/legislation/eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu#art-1.html",
      { headers: { Accept: "text/html" } },
    );
    expect(mockFetch).toHaveBeenCalledWith(
      "/v1/article/eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu#art-1/legislations",
    );
  });

  it.each([
    ["both dates", "2020-01-01/2030-12-31", "01.01.2020 - 31.12.2030"],
    ["only a from date", "2020-01-01/..", "gültig ab 01.01.2020"],
    ["only a to date", "../2030-12-31", "gültig bis 31.12.2030"],
  ])(
    "links the only Gesamtausgabe with %s",
    async (_, temporalCoverage, validity) => {
      const identifier =
        "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1";
      mockBackend({
        gesamtausgaben: [createGesamtausgabe(identifier, temporalCoverage)],
      });
      const user = userEvent.setup();
      await renderSuspended(ArtikelFassungenList, { props: props() });

      await user.click(screen.getByText("01.01.2031"));

      const link = screen.getByRole("link", {
        name: `Gesamtausgabe ${validity} öffnen`,
      });
      expect(link).toHaveAttribute("href", `/gesetze/${identifier}`);
      expect(
        screen.queryByRole("button", { name: "Gesamtausgabe auswählen" }),
      ).not.toBeInTheDocument();
    },
  );

  it("links the only Gesamtausgabe without dates when it has no validity", async () => {
    mockBackend({
      gesamtausgaben: [createGesamtausgabe("eli/bund/test", "../..")],
    });
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2031"));

    expect(
      screen.getByRole("link", { name: "Gesamtausgabe öffnen" }),
    ).toHaveAttribute("href", "/gesetze/eli/bund/test");
  });

  it("lets the user pick from a table when there are multiple Gesamtausgaben", async () => {
    const identifiers = [
      "eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu/regelungstext-1",
      "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu/regelungstext-1",
    ];
    mockBackend({
      gesamtausgaben: [
        createGesamtausgabe(identifiers[0]!, "2031-01-01/.."),
        createGesamtausgabe(identifiers[1]!, "2020-01-01/2030-12-31"),
      ],
    });
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2031"));

    expect(
      screen.queryByRole("link", { name: /öffnen$/ }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("list", { name: "Gesamtausgaben" }),
    ).not.toBeInTheDocument();

    await user.click(
      screen.getByRole("button", { name: "Gesamtausgabe auswählen" }),
    );

    const table = screen.getByRole("list", { name: "Gesamtausgaben" });
    const hrefs = within(table)
      .getAllByRole("link")
      .map((link) => link.getAttribute("href"));
    expect(hrefs).toEqual(identifiers.map((id) => `/gesetze/${id}`));
  });

  it("marks the Gesamtausgabe currently displayed as the current page", async () => {
    const currentGesamtausgabe = createGesamtausgabe(
      currentLegislationIdentifier,
      "2020-01-01/2030-12-31",
    );
    mockBackend({ gesamtausgaben: [gesamtausgabe, currentGesamtausgabe] });
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2031"));
    await user.click(
      screen.getByRole("button", { name: "Gesamtausgabe auswählen" }),
    );

    const table = screen.getByRole("list", { name: "Gesamtausgaben" });
    const [otherLink, currentLink] = within(table).getAllByRole("link");
    expect(otherLink).not.toHaveAttribute("aria-current");
    expect(currentLink).toHaveAttribute("aria-current", "page");
  });

  describe("with a date filter", () => {
    const earlierIdentifier = "eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu";
    const laterIdentifier = "eli/bund/bgbl-1/2000/s001/2032-01-01/1/deu";

    beforeEach(() => {
      mockBackend({
        gesamtausgaben: [
          createGesamtausgabe(earlierIdentifier, "2031-01-01/2031-12-31"),
          createGesamtausgabe(laterIdentifier, "2032-01-01/.."),
        ],
      });
    });

    it("links only the Gesamtausgabe valid on the filter date", async () => {
      const { rerender } = await renderSuspended(ArtikelFassungenList, {
        props: props(),
      });

      // The page filters the fassungen by the same date, leaving one row
      await rerender({ ...props([futureFassung]), dateFilter: "2031-06-01" });

      expect(
        await screen.findByRole("link", {
          name: "Gesamtausgabe 01.01.2031 - 31.12.2031 öffnen",
        }),
      ).toHaveAttribute("href", `/gesetze/${earlierIdentifier}`);
      expect(
        screen.queryByRole("button", { name: "Gesamtausgabe auswählen" }),
      ).not.toBeInTheDocument();
    });

    it("updates the linked Gesamtausgabe when the filter date changes", async () => {
      const { rerender } = await renderSuspended(ArtikelFassungenList, {
        props: props(),
      });
      await rerender({ ...props([futureFassung]), dateFilter: "2031-06-01" });
      await screen.findByRole("link", { name: /^Gesamtausgabe/ });

      await rerender({ ...props([futureFassung]), dateFilter: "2032-06-01" });

      expect(
        screen.getByRole("link", {
          name: "Gesamtausgabe gültig ab 01.01.2032 öffnen",
        }),
      ).toHaveAttribute("href", `/gesetze/${laterIdentifier}`);
    });
  });

  it("shows the fetched HTML once it becomes available", async () => {
    mockBackend();
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });
    const futureFassungRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureFassungRow).getByText("01.01.2031"));

    expect(within(futureFassungRow).getByText("Norm content")).toBeVisible();
  });

  it("shows an error message when fetching the content fails", async () => {
    mockFetch.mockRejectedValue(new Error("request failed"));
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });
    const futureFassungRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureFassungRow).getByText("01.01.2031"));

    const errorMessage = within(futureFassungRow).getByRole("alert");
    expect(errorMessage).toHaveTextContent("Es ist ein Fehler aufgetreten.");
  });

  it("hides the content again when the accordion is collapsed", async () => {
    mockFetch.mockReturnValue(new Promise(() => {}));
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });
    const futureFassungRow = screen.getAllByRole("listitem")[0]!;
    const toggle = within(futureFassungRow).getByText("01.01.2031");

    await user.click(toggle);
    expect(within(futureFassungRow).getByLabelText("Ladestatus")).toBeVisible();

    await user.click(toggle);

    expect(
      within(futureFassungRow).queryByLabelText("Ladestatus"),
    ).not.toBeInTheDocument();
  });

  it("shows a placeholder when there are no fassungen", async () => {
    await renderSuspended(ArtikelFassungenList, { props: props([]) });

    expect(screen.getByText("Keine Ergebnisse gefunden")).toBeInTheDocument();
    expect(screen.queryAllByRole("group")).toHaveLength(0);
  });

  it("does not announce anything before the fassungen change", async () => {
    await renderSuspended(ArtikelFassungenList, { props: props() });

    expect(screen.getByRole("status")).toHaveTextContent("");
  });

  it("announces when the fassungen change to none", async () => {
    const { rerender } = await renderSuspended(ArtikelFassungenList, {
      props: props(),
    });

    await rerender(props([]));

    expect(screen.getByRole("status")).toHaveTextContent(
      "Keine Ergebnisse gefunden",
    );
  });

  it("announces how many fassungen there are once there are some again", async () => {
    mockBackend();
    const { rerender } = await renderSuspended(ArtikelFassungenList, {
      props: props([]),
    });

    await rerender(props([pastFassung]));
    // the only row expands, so wait for its loading spinner to go away
    await screen.findByText("Norm content");

    expect(screen.getByRole("status")).toHaveTextContent("1 Fassung");

    await rerender(props());

    expect(screen.getByRole("status")).toHaveTextContent("3 Fassungen");
  });

  it("closes the previously expanded row when another one is expanded", async () => {
    mockFetch.mockImplementation(async (url: string) => {
      if (url.endsWith("/legislations")) return { member: [gesamtausgabe] };
      return url.includes("2031")
        ? "<html><body>Future content</body></html>"
        : "<html><body>Past content</body></html>";
    });
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2031"));
    await user.click(screen.getByText("05.01.2000"));

    expect(screen.queryByText("Future content")).not.toBeInTheDocument();
    expect(screen.getByText("Past content")).toBeVisible();
  });

  it("keeps the row expanded when clicking inside its content", async () => {
    mockBackend();
    const user = userEvent.setup();
    await renderSuspended(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2031"));
    await user.click(screen.getByText("Norm content"));

    expect(screen.getByText("Norm content")).toBeVisible();
  });

  it("expands the only remaining row when filtering and collapses it again when the filter is cleared", async () => {
    mockBackend();
    const { rerender } = await renderSuspended(ArtikelFassungenList, {
      props: props(),
    });

    await rerender(props([pastFassung]));

    expect(await screen.findByText("Norm content")).toBeVisible();

    await rerender(props());

    expect(screen.queryByText("Norm content")).not.toBeInTheDocument();
  });

  it("expands the current fassung when it is the only remaining row", async () => {
    mockBackend();
    const { rerender } = await renderSuspended(ArtikelFassungenList, {
      props: props(),
    });

    await rerender(props([currentFassung]));

    expect(
      await screen.findByRole("link", { name: /^Gesamtausgabe/ }),
    ).toBeVisible();
  });
});
