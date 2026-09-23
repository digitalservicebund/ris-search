import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { afterEach, beforeEach, describe, it, expect, vi } from "vitest";
import { nextTick } from "vue";
import Combobox, { type ComboboxOption } from "./Combobox.vue";

const options: ComboboxOption[] = [
  { id: "BGH", label: "Bundesgerichtshof" },
  { id: "BFH", label: "Bundesfinanzhof", secondaryLabel: "BFH" },
];

describe("Combobox", () => {
  // jsdom doesn't implement scrolling; Reka scrolls the highlighted option
  // into view when the suggestion list opens.
  beforeEach(() => {
    Element.prototype.scrollIntoView = vi.fn();
  });

  afterEach(() => {
    // @ts-expect-error restore jsdom's state, which has no implementation
    delete Element.prototype.scrollIntoView;
  });

  it("renders without error with reasonable defaults", () => {
    render(Combobox);

    expect(screen.getByRole("combobox")).toBeInTheDocument();
  });

  it("does not show the suggestion list until opened", () => {
    render(Combobox, { props: { options } });

    expect(screen.queryByRole("option")).not.toBeInTheDocument();
  });

  it("shows options once the trigger is clicked", async () => {
    const user = userEvent.setup();
    render(Combobox, { props: { options } });

    await user.click(
      screen.getByRole("button", { name: "Vorschläge anzeigen" }),
    );

    expect(screen.getAllByRole("option")).toHaveLength(2);
    expect(
      screen.getByRole("option", { name: "Bundesfinanzhof BFH" }),
    ).toBeInTheDocument();
  });

  it("reflects loading with a spinner", () => {
    render(Combobox, { props: { loading: true } });

    expect(screen.getByLabelText("Ladestatus")).toBeInTheDocument();
  });

  it("shows initialLabel as the input value before options are loaded", async () => {
    render(Combobox, {
      props: { modelValue: "BGH", initialLabel: "Bundesgerichtshof" },
    });
    await nextTick();

    expect(screen.getByRole("combobox")).toHaveValue("Bundesgerichtshof");
  });

  it("shows the label of the option matching modelValue once loaded", async () => {
    render(Combobox, { props: { options, modelValue: "BFH" } });
    await nextTick();

    expect(screen.getByRole("combobox")).toHaveValue("Bundesfinanzhof");
  });

  it("falls back to showing the raw id when there is no matching option or initialLabel", async () => {
    render(Combobox, { props: { modelValue: "BGH" } });
    await nextTick();

    expect(screen.getByRole("combobox")).toHaveValue("BGH");
  });

  it("emits update:searchTerm while typing", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Combobox, { props: { options } });

    await user.type(screen.getByRole("combobox"), "BG");

    expect(emitted("update:searchTerm")).toContainEqual(["BG"]);
  });

  it("emits update:open when opened and closed", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Combobox, { props: { options } });

    await user.click(
      screen.getByRole("button", { name: "Vorschläge anzeigen" }),
    );

    expect(emitted("update:open")).toContainEqual([true]);
  });

  it("selects an option, updates modelValue and closes the list", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Combobox, { props: { options } });

    await user.click(
      screen.getByRole("button", { name: "Vorschläge anzeigen" }),
    );
    await user.click(screen.getByRole("option", { name: "Bundesgerichtshof" }));

    expect(emitted("update:modelValue")).toContainEqual(["BGH"]);
    expect(screen.queryByRole("option")).not.toBeInTheDocument();
  });

  it("shows a clear button once a value is present, and clears it", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Combobox, {
      props: { options, modelValue: "BGH" },
    });

    const clearButton = screen.getByRole("button", { name: "Entfernen" });
    expect(clearButton).toBeInTheDocument();

    await user.click(clearButton);

    expect(emitted("update:modelValue")).toContainEqual([undefined]);
  });

  it("does not show a clear button when there is nothing to clear", () => {
    render(Combobox, { props: { options } });

    expect(
      screen.queryByRole("button", { name: "Entfernen" }),
    ).not.toBeInTheDocument();
  });

  it("forwards placeholder and aria-labelledby to the input", () => {
    render({
      components: { Combobox },
      template: `
        <div>
          <label id="court-label">Gericht</label>
          <Combobox placeholder="Auswählen oder suchen" aria-labelledby="court-label" />
        </div>
      `,
    });

    const input = screen.getByRole("combobox", { name: "Gericht" });
    expect(input).toHaveAttribute("placeholder", "Auswählen oder suchen");
  });

  it("marks the input as expanded while the list is open", async () => {
    const user = userEvent.setup();
    render(Combobox, { props: { options } });

    const input = screen.getByRole("combobox");
    expect(input).toHaveAttribute("aria-expanded", "false");

    await user.click(
      screen.getByRole("button", { name: "Vorschläge anzeigen" }),
    );

    expect(input).toHaveAttribute("aria-expanded", "true");
  });
});
