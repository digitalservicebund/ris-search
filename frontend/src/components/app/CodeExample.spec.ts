import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen, within } from "@testing-library/vue";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import CodeExample from "./CodeExample.vue";

// The code is spelled out as markup in the template, these are the same
// snippets as plain text
const curlCode =
  'curl "https://testphase.rechtsinformationen.bund.de/v1/document?size=10"';

const javascriptCode = [
  'const url = "https://testphase.rechtsinformationen.bund.de/v1/document";',
  "const response = await fetch(`${url}?size=10`);",
  "const { totalItems, member } = await response.json();",
].join("\n");

const tab = (name: string) => screen.getByRole("tab", { name });
const panel = () => screen.getByRole("tabpanel");
const copyButton = (name = "Kopieren") => screen.getByRole("button", { name });

describe("CodeExample", () => {
  // jsdom implements neither the clipboard nor the permission prompt for it
  const writeText = vi.fn().mockResolvedValue(undefined);

  beforeEach(() => {
    vi.stubGlobal("navigator", { ...navigator, clipboard: { writeText } });
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    vi.clearAllMocks();
  });

  it("renders a tab per example and shows the first one", async () => {
    await renderSuspended(CodeExample);

    expect(screen.getAllByRole("tab")).toHaveLength(2);
    expect(tab("cURL")).toHaveAttribute("aria-selected", "true");
    expect(panel()).toHaveTextContent(curlCode);
  });

  it("labels the tab list and marks its orientation", async () => {
    await renderSuspended(CodeExample);

    expect(
      screen.getByRole("tablist", { name: "Programmiersprache des Beispiels" }),
    ).toHaveAttribute("aria-orientation", "horizontal");
  });

  it("links each tab to its panel", async () => {
    await renderSuspended(CodeExample);

    expect(tab("JavaScript")).toHaveAttribute("aria-selected", "false");
    expect(panel()).toHaveAttribute(
      "id",
      tab("cURL").getAttribute("aria-controls"),
    );
    expect(panel()).toHaveAttribute(
      "aria-labelledby",
      tab("cURL").getAttribute("id"),
    );
  });

  it("makes only the selected tab reachable with the tab key", async () => {
    await renderSuspended(CodeExample);

    expect(tab("cURL")).toHaveAttribute("tabindex", "0");
    expect(tab("JavaScript")).toHaveAttribute("tabindex", "-1");
  });

  it("renders the same code that it copies, behind a prompt", async () => {
    await renderSuspended(CodeExample);

    expect(panel().textContent).toBe(`$ ${curlCode}`);

    await userEvent.click(tab("JavaScript"));

    expect(panel().textContent).toBe(javascriptCode);
  });

  it("hides the prompt from screen readers", async () => {
    await renderSuspended(CodeExample);

    expect(within(panel()).getByText("$")).toHaveAttribute(
      "aria-hidden",
      "true",
    );
  });

  it("shows the example of the tab that is clicked", async () => {
    await renderSuspended(CodeExample);

    await userEvent.click(tab("JavaScript"));

    expect(tab("JavaScript")).toHaveAttribute("aria-selected", "true");
    expect(panel()).toHaveTextContent("await fetch");
  });

  it("selects the next and previous tab with the arrow keys, wrapping around", async () => {
    await renderSuspended(CodeExample);

    tab("cURL").focus();

    await userEvent.keyboard("{ArrowRight}");
    expect(tab("JavaScript")).toHaveFocus();
    expect(tab("JavaScript")).toHaveAttribute("aria-selected", "true");

    await userEvent.keyboard("{ArrowRight}");
    expect(tab("cURL")).toHaveFocus();

    await userEvent.keyboard("{ArrowLeft}");
    expect(tab("JavaScript")).toHaveFocus();
  });

  it("selects the first and last tab with Home and End", async () => {
    await renderSuspended(CodeExample);

    tab("cURL").focus();

    await userEvent.keyboard("{End}");
    expect(tab("JavaScript")).toHaveAttribute("aria-selected", "true");

    await userEvent.keyboard("{Home}");
    expect(tab("cURL")).toHaveAttribute("aria-selected", "true");
  });

  it("copies the shown code, without the prompt", async () => {
    await renderSuspended(CodeExample);

    await userEvent.click(copyButton());

    expect(writeText).toHaveBeenCalledWith(curlCode);
  });

  it("copies the code of the tab that is selected", async () => {
    await renderSuspended(CodeExample);

    await userEvent.click(tab("JavaScript"));
    await userEvent.click(copyButton());

    expect(writeText).toHaveBeenCalledWith(javascriptCode);
  });

  it("confirms copying, then returns to its initial label", async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    await renderSuspended(CodeExample);

    await userEvent.click(copyButton());

    expect(copyButton("Kopiert")).toBeVisible();
    expect(screen.getByRole("status")).toHaveTextContent("Kopiert");

    await vi.advanceTimersByTimeAsync(6000);

    expect(copyButton()).toBeVisible();
    expect(screen.getByRole("status")).toBeEmptyDOMElement();
    vi.useRealTimers();
  });

  it("keeps its initial label when copying fails", async () => {
    writeText.mockRejectedValueOnce(new Error("not allowed"));
    await renderSuspended(CodeExample);

    await userEvent.click(copyButton());

    expect(copyButton()).toBeVisible();
  });
});
