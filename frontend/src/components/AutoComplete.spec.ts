import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AutoComplete from "./AutoComplete.vue";
import type { AutoCompleteSuggestion } from "./AutoComplete.vue";

describe("AutoComplete", () => {
  it("renders without error with default props", async () => {
    await renderSuspended(AutoComplete);
    expect(document.querySelector("input")).toBeInTheDocument();
  });

  it("renders the ProgressSpinner when loading", async () => {
    await renderSuspended(AutoComplete, { props: { loading: true } });
    expect(document.querySelector(".p-progressspinner")).toBeInTheDocument();
  });

  it("renders the dropdown button", async () => {
    await renderSuspended(AutoComplete, { props: { dropdown: true } });
    expect(
      screen.getByRole("button", { name: "Vorschläge anzeigen" }),
    ).toBeInTheDocument();
  });

  it("renders the clear button when a value is set", async () => {
    const suggestions: AutoCompleteSuggestion[] = [
      { id: "1", label: "Option 1" },
    ];
    await renderSuspended(AutoComplete, {
      props: { dropdown: true, initialLabel: "Option 1", suggestions },
    });
    expect(
      screen.getByRole("button", { name: "Entfernen" }),
    ).toBeInTheDocument();
  });

  it("uses initialLabel as the displayed value", async () => {
    await renderSuspended(AutoComplete, {
      props: { initialLabel: "Preselected Value" },
    });
    const input = document.querySelector("input") as HTMLInputElement;
    expect(input.value).toBe("Preselected Value");
  });

  it("forwards placeholder prop to the input", async () => {
    await renderSuspended(AutoComplete, {
      props: { placeholder: "Search here" },
    });
    expect(screen.getByPlaceholderText("Search here")).toBeInTheDocument();
  });
});
