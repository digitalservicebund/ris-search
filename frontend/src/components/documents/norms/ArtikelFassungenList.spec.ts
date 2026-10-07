import { userEvent } from "@testing-library/user-event";
import { render, screen, within } from "@testing-library/vue";
import type { FetchHook } from "ofetch";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import type { ArtikelFassung } from "~/types/api.ts";
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
  isPartOf: string[] = [],
): ArtikelFassung {
  return {
    "@id": identifier,
    eId: "art-1",
    name: "§ 1",
    temporalCoverage,
    isPartOf: isPartOf.map((id) => ({ "@id": id })),
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

const currentExpressionId =
  "/v1/legislation/eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu";

const futureFassung = createFassung(
  "eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu#art-1",
  "2031-01-01/..",
);
const currentFassung = createFassung(
  "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu#art-1",
  "2020-01-01/2030-12-31",
  [currentExpressionId],
);
const pastFassung = createFassung(
  "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu#art-1",
  "2000-01-05/2019-12-31",
);

/** Props for the list, with the middle fassung being the displayed one. */
function props(
  fassungen: ArtikelFassung[] = [pastFassung, currentFassung, futureFassung],
) {
  return {
    currentExpressionId: currentExpressionId,
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

  it("lists fassungen, sorted by date, newest first", () => {
    render(ArtikelFassungenList, { props: props() });

    const rows = screen.getAllByRole("listitem");
    expect(rows).toHaveLength(3);

    expect(within(rows[0]!).getByText("01.01.2031")).toBeInTheDocument();
    expect(within(rows[0]!).getByText("—")).toBeInTheDocument();

    expect(within(rows[1]!).getByText("01.01.2020")).toBeInTheDocument();
    expect(within(rows[1]!).getByText("31.12.2030")).toBeInTheDocument();

    expect(within(rows[2]!).getByText("05.01.2000")).toBeInTheDocument();
    expect(within(rows[2]!).getByText("31.12.2019")).toBeInTheDocument();
  });

  it("joins the dates with a dash", () => {
    render(ArtikelFassungenList, { props: props() });

    for (const row of screen.getAllByRole("listitem")) {
      expect(within(row).getByText("–")).toHaveAttribute("aria-hidden", "true");
    }
  });

  it("shows the column labels as a header, but keeps it from SR", () => {
    render(ArtikelFassungenList, { props: props() });

    expect(screen.getByText("Gültig ab")).toBeInTheDocument();
    expect(screen.getByText("Gültig bis")).toBeInTheDocument();

    // Only the 3 fassung rows are exposed to screen readers, not the header
    expect(screen.getAllByRole("listitem")).toHaveLength(3);
  });

  it("marks the current fassung as the current entry", () => {
    render(ArtikelFassungenList, { props: props() });

    const currentRow = screen.getByRole("group", { current: true });
    expect(currentRow).toHaveTextContent(
      "Gültig ab: 01.01.2020– Gültig bis: 31.12.2030",
    );
  });

  it("current fassung is not expandable", async () => {
    const user = userEvent.setup();
    render(ArtikelFassungenList, { props: props() });

    const currentRow = screen.getByRole("group", { current: true });

    await user.click(currentRow);

    expect(within(currentRow).queryByRole("status")).not.toBeInTheDocument();
  });

  it("renders the other fassungen as a closed accordion by default", () => {
    render(ArtikelFassungenList, { props: props() });

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

  it("shows a loading spinner while the HTML is being fetched", async () => {
    mockFetch.mockReturnValue(new Promise(() => {}));
    const user = userEvent.setup();
    render(ArtikelFassungenList, { props: props() });
    const futureFassungRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureFassungRow).getByText("01.01.2031"));

    expect(within(futureFassungRow).getByLabelText("Ladestatus")).toBeVisible();
  });

  it("shows the fetched HTML once it becomes available", async () => {
    mockFetch.mockResolvedValueOnce(
      "<html><body><p>Norm content</p></body></html>",
    );
    const user = userEvent.setup();
    render(ArtikelFassungenList, { props: props() });
    const futureFassungRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureFassungRow).getByText("01.01.2031"));

    expect(within(futureFassungRow).getByText("Norm content")).toBeVisible();
  });

  it("shows an error message when fetching the HTML fails", async () => {
    mockFetch.mockRejectedValueOnce(new Error("request failed"));
    const user = userEvent.setup();
    render(ArtikelFassungenList, { props: props() });
    const futureFassungRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureFassungRow).getByText("01.01.2031"));

    const errorMessage = within(futureFassungRow).getByRole("alert");
    expect(errorMessage).toHaveTextContent("Es ist ein Fehler aufgetreten.");
  });

  it("hides the content again when the accordion is collapsed", async () => {
    mockFetch.mockReturnValue(new Promise(() => {}));
    const user = userEvent.setup();
    render(ArtikelFassungenList, { props: props() });
    const futureFassungRow = screen.getAllByRole("listitem")[0]!;
    const toggle = within(futureFassungRow).getByText("01.01.2031");

    await user.click(toggle);
    expect(within(futureFassungRow).getByLabelText("Ladestatus")).toBeVisible();

    await user.click(toggle);

    expect(
      within(futureFassungRow).queryByLabelText("Ladestatus"),
    ).not.toBeInTheDocument();
  });

  it("shows a placeholder when there are no fassungen", () => {
    render(ArtikelFassungenList, { props: props([]) });

    expect(screen.getByText("Keine Ergebnisse gefunden")).toBeInTheDocument();
    expect(screen.queryAllByRole("group")).toHaveLength(0);
  });

  it("does not announce anything before the fassungen change", () => {
    render(ArtikelFassungenList, { props: props() });

    expect(screen.getByRole("status")).toHaveTextContent("");
  });

  it("announces when the fassungen change to none", async () => {
    const { rerender } = render(ArtikelFassungenList, { props: props() });

    await rerender(props([]));

    expect(screen.getByRole("status")).toHaveTextContent(
      "Keine Ergebnisse gefunden",
    );
  });

  it("announces how many fassungen there are once there are some again", async () => {
    mockFetch.mockResolvedValueOnce("<html><body>Content</body></html>");
    const { rerender } = render(ArtikelFassungenList, {
      props: props([]),
    });

    await rerender(props([pastFassung]));
    await nextTick();

    expect(screen.getByRole("status")).toHaveTextContent("1 Fassung");

    await rerender(props());

    expect(screen.getByRole("status")).toHaveTextContent("3 Fassungen");
  });

  it("closes the previously expanded row when another one is expanded", async () => {
    mockFetch.mockResolvedValueOnce("<html><body>Future content</body></html>");
    mockFetch.mockResolvedValueOnce("<html><body>Past content</body></html>");
    const user = userEvent.setup();
    render(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2031"));
    await user.click(screen.getByText("05.01.2000"));

    expect(screen.queryByText("Future content")).not.toBeInTheDocument();
    expect(screen.getByText("Past content")).toBeVisible();
  });

  it("keeps the row expanded when clicking inside its content", async () => {
    mockFetch.mockResolvedValueOnce(
      "<html><body><p>Norm content</p></body></html>",
    );
    const user = userEvent.setup();
    render(ArtikelFassungenList, { props: props() });

    await user.click(screen.getByText("01.01.2031"));
    await user.click(screen.getByText("Norm content"));

    expect(screen.getByText("Norm content")).toBeVisible();
  });

  it("expands the only remaining row when filtering and collapses it again when the filter is cleared", async () => {
    mockFetch.mockResolvedValueOnce(
      "<html><body><p>Norm content</p></body></html>",
    );
    const { rerender } = render(ArtikelFassungenList, { props: props() });

    await rerender(props([pastFassung]));
    await nextTick();

    expect(screen.getByText("Norm content")).toBeVisible();

    await rerender(props());

    expect(screen.queryByText("Norm content")).not.toBeInTheDocument();
  });

  it("does not load the current fassung when it is the only remaining row", async () => {
    const { rerender } = render(ArtikelFassungenList, { props: props() });

    await rerender(props([currentFassung]));

    expect(mockFetch).not.toHaveBeenCalled();
    expect(screen.queryByLabelText("Ladestatus")).not.toBeInTheDocument();
  });
});
