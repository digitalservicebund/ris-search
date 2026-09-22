import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { render, screen, within } from "@testing-library/vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { SingleNorm } from "~/composables/useSingleNormVersions";
import type { HtmlCacheEntry } from "~/composables/useSingleNormVersionsHtml";
import SingleNormVersionList from "./SingleNormVersionList.vue";

/**
 * The native `toggle` event for `<details>` is queued as a task rather than
 * fired synchronously, so awaiting the click alone isn't enough to observe its
 * effects (see https://html.spec.whatwg.org/#dom-details-open).
 */
function flushToggleEvent() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

const { updateRowsHtmlMock } = vi.hoisted(() => {
  return {
    updateRowsHtmlMock: vi.fn(),
  };
});

const rowsHtml = ref(new Map<string, HtmlCacheEntry>());

mockNuxtImport("useSingleNormVersionsHtml", () => {
  return () => ({ rowsHtml, updateRowsHtml: updateRowsHtmlMock });
});

function createSingleNorm(
  identifier: string,
  temporalCoverage: string,
): SingleNorm {
  return {
    "@id": identifier,
    eId: "art-1",
    temporalCoverage,
    isPartOf: [{ "@id": identifier.split("#")[0]! }],
    encoding: { contentUrl: `/v1/legislation/${identifier}.html` },
  };
}

const futureVersion = createSingleNorm(
  "eli/bund/bgbl-1/2000/s001/2031-01-01/1/deu#art-1",
  "2031-01-01/..",
);
const currentVersion = createSingleNorm(
  "eli/bund/bgbl-1/2000/s001/2020-01-01/1/deu#art-1",
  "2020-01-01/2030-12-31",
);
const pastVersion = createSingleNorm(
  "eli/bund/bgbl-1/2000/s001/2000-01-01/1/deu#art-1",
  "2000-01-05/2019-12-31",
);

/** Props for the list, with the middle version being the displayed one. */
function props(
  versions: SingleNorm[] = [pastVersion, currentVersion, futureVersion],
) {
  return {
    currentSingleNormIdentifier: currentVersion["@id"],
    versions,
  };
}

describe("SingleNormVersionList", () => {
  beforeEach(() => {
    rowsHtml.value = new Map();
    updateRowsHtmlMock.mockReset();
  });

  it("lists versions, sorted by date, newest first", () => {
    render(SingleNormVersionList, { props: props() });

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
    render(SingleNormVersionList, { props: props() });

    expect(screen.getByText("Gültig ab")).toBeInTheDocument();
    expect(screen.getByText("Gültig bis")).toBeInTheDocument();

    // Only the 3 version rows are exposed to screen readers, not the header
    expect(screen.getAllByRole("listitem")).toHaveLength(3);
  });

  it("marks the current version as the current entry", () => {
    render(SingleNormVersionList, { props: props() });

    const currentRow = screen.getByRole("group", { current: true });
    expect(currentRow).toHaveTextContent(
      "Gültig ab: 01.01.2020 Gültig bis: 31.12.2030",
    );
  });

  it("current version is not expandable", async () => {
    const user = userEvent.setup();
    render(SingleNormVersionList, { props: props() });

    const currentRow = screen.getByRole("group", { current: true });

    await user.click(currentRow);
    await flushToggleEvent();

    expect(updateRowsHtmlMock).not.toHaveBeenCalled();
  });

  it("renders the other versions as a closed accordion by default", () => {
    render(SingleNormVersionList, { props: props() });

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

  it("requests the HTML for a version when it's expanded", async () => {
    const user = userEvent.setup();
    render(SingleNormVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureVersionRow).getByText("01.01.2031"));
    await flushToggleEvent();

    expect(updateRowsHtmlMock).toHaveBeenCalledWith(
      futureVersion["@id"],
      futureVersion.encoding.contentUrl,
    );
  });

  it("shows a loading spinner while the HTML is being fetched", async () => {
    const user = userEvent.setup();
    render(SingleNormVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureVersionRow).getByText("01.01.2031"));
    await flushToggleEvent();

    expect(within(futureVersionRow).getByLabelText("Ladestatus")).toBeVisible();
  });

  it("shows the fetched HTML once it becomes available", async () => {
    updateRowsHtmlMock.mockImplementation(async (rowKey: string) => {
      rowsHtml.value.set(rowKey, {
        html: "<p>Norm content</p>",
        error: false,
      });
    });
    const user = userEvent.setup();
    render(SingleNormVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureVersionRow).getByText("01.01.2031"));
    await flushToggleEvent();

    expect(within(futureVersionRow).getByText("Norm content")).toBeVisible();
  });

  it("shows an error message when fetching the HTML fails", async () => {
    updateRowsHtmlMock.mockImplementation(async (rowKey: string) => {
      rowsHtml.value.set(rowKey, { error: true });
    });
    const user = userEvent.setup();
    render(SingleNormVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureVersionRow).getByText("01.01.2031"));
    await flushToggleEvent();

    const errorMessage = within(futureVersionRow).getByRole("alert");
    expect(errorMessage).toHaveTextContent("Es ist ein Fehler aufgetreten.");
  });

  it("hides the content again when the accordion is collapsed", async () => {
    const user = userEvent.setup();
    render(SingleNormVersionList, { props: props() });
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
    render(SingleNormVersionList, { props: props([]) });

    expect(screen.getByText("Keine Ergebnisse gefunden")).toBeInTheDocument();
    expect(screen.queryAllByRole("group")).toHaveLength(0);
  });

  it("does not announce anything before the versions change", () => {
    render(SingleNormVersionList, { props: props() });

    expect(screen.getByRole("status")).toHaveTextContent("");
  });

  it("announces when the versions change to none", async () => {
    const { rerender } = render(SingleNormVersionList, { props: props() });

    await rerender(props([]));

    expect(screen.getByRole("status")).toHaveTextContent(
      "Keine Ergebnisse gefunden",
    );
  });

  it("announces how many versions there are once there are some again", async () => {
    const { rerender } = render(SingleNormVersionList, {
      props: props([]),
    });

    await rerender(props([pastVersion]));

    expect(screen.getByRole("status")).toHaveTextContent("1 Ergebnis");

    await rerender(props());

    expect(screen.getByRole("status")).toHaveTextContent("3 Ergebnisse");
  });
});
