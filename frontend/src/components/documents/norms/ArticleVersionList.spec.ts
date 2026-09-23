import { userEvent } from "@testing-library/user-event";
import { render, screen, within } from "@testing-library/vue";
import type { FetchHook } from "ofetch";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { ArticleVersion } from "~/types/api.ts";
import ArticleVersionList from "./ArticleVersionList.vue";

/**
 * The native `toggle` event for `<details>` is queued as a task rather than
 * fired synchronously, so awaiting the click alone isn't enough to observe its
 * effects (see https://html.spec.whatwg.org/#dom-details-open).
 */
function flushToggleEvent() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

function createArticleVersion(
  identifier: string,
  temporalCoverage: string,
  isPartOf: string[] = [],
): ArticleVersion {
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

const futureVersion = createArticleVersion(
  "eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu#art-1",
  "2031-01-01/..",
);
const currentVersion = createArticleVersion(
  "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu#art-1",
  "2020-01-01/2030-12-31",
  [currentExpressionId],
);
const pastVersion = createArticleVersion(
  "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu#art-1",
  "2000-01-05/2019-12-31",
);

/** Props for the list, with the middle version being the displayed one. */
function props(
  versions: ArticleVersion[] = [pastVersion, currentVersion, futureVersion],
) {
  return {
    currentExpressionId: currentExpressionId,
    versions,
  };
}

describe("ArticleVersionList", () => {
  beforeEach(() => {
    mockFetch.mockReset();
  });

  it("lists versions, sorted by date, newest first", () => {
    render(ArticleVersionList, { props: props() });

    const rows = screen.getAllByRole("listitem");
    expect(rows).toHaveLength(3);

    expect(within(rows[0]!).getByText("01.01.2031")).toBeInTheDocument();
    expect(within(rows[0]!).getByText("–")).toBeInTheDocument();

    expect(within(rows[1]!).getByText("01.01.2020")).toBeInTheDocument();
    expect(within(rows[1]!).getByText("31.12.2030")).toBeInTheDocument();

    expect(within(rows[2]!).getByText("05.01.2000")).toBeInTheDocument();
    expect(within(rows[2]!).getByText("31.12.2019")).toBeInTheDocument();
  });

  it("shows the column labels as a header, but keeps it from SR", () => {
    render(ArticleVersionList, { props: props() });

    expect(screen.getByText("Gültig ab")).toBeInTheDocument();
    expect(screen.getByText("Gültig bis")).toBeInTheDocument();

    // Only the 3 version rows are exposed to screen readers, not the header
    expect(screen.getAllByRole("listitem")).toHaveLength(3);
  });

  it("marks the current version as the current entry", () => {
    render(ArticleVersionList, { props: props() });

    const currentRow = screen.getByRole("group", { current: true });
    expect(currentRow).toHaveTextContent(
      "Gültig ab: 01.01.2020 Gültig bis: 31.12.2030",
    );
  });

  it("current version is not expandable", async () => {
    const user = userEvent.setup();
    render(ArticleVersionList, { props: props() });

    const currentRow = screen.getByRole("group", { current: true });

    await user.click(currentRow);
    await flushToggleEvent();

    expect(within(currentRow).queryByRole("status")).not.toBeInTheDocument();
  });

  it("renders the other versions as a closed accordion by default", () => {
    render(ArticleVersionList, { props: props() });

    const rows = screen.getAllByRole("listitem");
    const futureVersionRow = rows[0]!;
    const pastVersionRow = rows[2]!;

    expect(
      within(futureVersionRow).queryByRole("status"),
    ).not.toBeInTheDocument();
    expect(
      within(pastVersionRow).queryByRole("status"),
    ).not.toBeInTheDocument();
  });

  it("shows a loading spinner while the HTML is being fetched", async () => {
    mockFetch.mockReturnValue(new Promise(() => {}));
    const user = userEvent.setup();
    render(ArticleVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureVersionRow).getByText("01.01.2031"));
    await flushToggleEvent();

    expect(within(futureVersionRow).getByLabelText("Ladestatus")).toBeVisible();
  });

  it("shows the fetched HTML once it becomes available", async () => {
    mockFetch.mockResolvedValueOnce(
      "<html><body><p>Norm content</p></body></html>",
    );
    const user = userEvent.setup();
    render(ArticleVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureVersionRow).getByText("01.01.2031"));
    await flushToggleEvent();

    expect(within(futureVersionRow).getByText("Norm content")).toBeVisible();
  });

  it("shows an error message when fetching the HTML fails", async () => {
    mockFetch.mockRejectedValueOnce(new Error("request failed"));
    const user = userEvent.setup();
    render(ArticleVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureVersionRow).getByText("01.01.2031"));
    await flushToggleEvent();

    const errorMessage = within(futureVersionRow).getByRole("alert");
    expect(errorMessage).toHaveTextContent("Es ist ein Fehler aufgetreten.");
  });

  it("hides the content again when the accordion is collapsed", async () => {
    mockFetch.mockReturnValue(new Promise(() => {}));
    const user = userEvent.setup();
    render(ArticleVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;
    const toggle = within(futureVersionRow).getByText("01.01.2031");

    await user.click(toggle);
    await flushToggleEvent();
    expect(within(futureVersionRow).getByLabelText("Ladestatus")).toBeVisible();

    await user.click(toggle);
    await flushToggleEvent();

    expect(
      within(futureVersionRow).queryByLabelText("Ladestatus"),
    ).not.toBeInTheDocument();
  });

  it("shows a placeholder when there are no versions", () => {
    render(ArticleVersionList, { props: props([]) });

    expect(screen.getByText("Keine Ergebnisse gefunden")).toBeInTheDocument();
    expect(screen.queryAllByRole("group")).toHaveLength(0);
  });

  it("does not announce anything before the versions change", () => {
    render(ArticleVersionList, { props: props() });

    expect(screen.getByRole("status")).toHaveTextContent("");
  });

  it("announces when the versions change to none", async () => {
    const { rerender } = render(ArticleVersionList, { props: props() });

    await rerender(props([]));

    expect(screen.getByRole("status")).toHaveTextContent(
      "Keine Ergebnisse gefunden",
    );
  });

  it("announces how many versions there are once there are some again", async () => {
    const { rerender } = render(ArticleVersionList, {
      props: props([]),
    });

    await rerender(props([pastVersion]));

    expect(screen.getByRole("status")).toHaveTextContent("1 Ergebnis");

    await rerender(props());

    expect(screen.getByRole("status")).toHaveTextContent("3 Ergebnisse");
  });
});
