import { userEvent } from "@testing-library/user-event";
import { render, screen, within } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import type { SingleNorm } from "~/composables/useSingleNormVersions";
import SingleNormVersionList from "./SingleNormVersionList.vue";

/**
 * The native `toggle` event for `<details>` is queued as a task rather than
 * fired synchronously, so awaiting the click alone isn't enough to observe its
 * effects (see https://html.spec.whatwg.org/#dom-details-open).
 */
function flushToggleEvent() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

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

    // Unlike the other versions, the current one has no expandable content
    expect(
      within(currentRow).queryByText("Html content coming soon..."),
    ).not.toBeInTheDocument();
  });

  it("current version is not expandable", async () => {
    const user = userEvent.setup();
    render(SingleNormVersionList, { props: props() });

    const currentRow = screen.getByRole("group", { current: true });

    await user.click(currentRow);
    await flushToggleEvent();

    expect(
      within(currentRow).queryByText("Html content coming soon..."),
    ).not.toBeInTheDocument();
  });

  it("renders the other versions as a closed accordion by default", () => {
    render(SingleNormVersionList, { props: props() });

    const rows = screen.getAllByRole("listitem");
    const futureVersionRow = rows[0]!;
    const pastVersionRow = rows[2]!;

    expect(
      within(futureVersionRow).getByText("Html content coming soon..."),
    ).not.toBeVisible();
    expect(
      within(pastVersionRow).getByText("Html content coming soon..."),
    ).not.toBeVisible();
  });

  it("expands a version and updates the model when it's opened", async () => {
    const user = userEvent.setup();
    const { emitted } = render(SingleNormVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;

    await user.click(within(futureVersionRow).getByText("01.01.2031"));
    await flushToggleEvent();

    expect(
      within(futureVersionRow).getByText("Html content coming soon..."),
    ).toBeVisible();
    expect(emitted("update:modelValue")?.at(-1)).toEqual([
      futureVersion["@id"],
    ]);
  });

  it("collapses a version and clears the model when it's closed again", async () => {
    const user = userEvent.setup();
    const { emitted } = render(SingleNormVersionList, { props: props() });
    const futureVersionRow = screen.getAllByRole("listitem")[0]!;
    const toggle = within(futureVersionRow).getByText("01.01.2031");

    await user.click(toggle);
    await flushToggleEvent();
    await user.click(toggle);
    await flushToggleEvent();

    expect(
      within(futureVersionRow).getByText("Html content coming soon..."),
    ).not.toBeVisible();
    expect(emitted("update:modelValue")?.at(-1)).toEqual([undefined]);
  });

  it("opens the version matching the model value", () => {
    render(SingleNormVersionList, {
      props: { ...props(), modelValue: futureVersion["@id"] },
    });

    const rows = screen.getAllByRole("listitem");
    const futureVersionRow = rows[0]!;
    const pastVersionRow = rows[2]!;

    expect(
      within(futureVersionRow).getByText("Html content coming soon..."),
    ).toBeVisible();
    expect(
      within(pastVersionRow).getByText("Html content coming soon..."),
    ).not.toBeVisible();
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
