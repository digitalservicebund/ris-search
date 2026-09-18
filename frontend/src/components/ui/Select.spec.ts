import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Select from "./Select.vue";

const objectOptions = [
  { label: "Relevanz", value: "default" },
  { label: "Datum: Älteste zuerst", value: "date" },
];

describe("Select", () => {
  it("renders a combobox", () => {
    render(Select, { props: { options: ["25", "50"] } });

    expect(screen.getByRole("combobox")).toBeInTheDocument();
  });

  it("renders plain string options as both label and value", () => {
    render(Select, { props: { options: ["25", "50", "100"] } });

    expect(screen.getAllByRole("option")).toHaveLength(3);
    expect(screen.getByRole("option", { name: "50" })).toHaveValue("50");
  });

  it("renders object options using their label and value", () => {
    render(Select, {
      props: {
        options: objectOptions,
      },
    });

    expect(screen.getByRole("option", { name: "Relevanz" })).toHaveValue(
      "default",
    );
    expect(
      screen.getByRole("option", { name: "Datum: Älteste zuerst" }),
    ).toHaveValue("date");
  });

  it("marks the option matching the model as selected", () => {
    render(Select, {
      props: {
        options: objectOptions,
        modelValue: "date",
      },
    });

    expect(screen.getByRole("combobox")).toHaveValue("date");
  });

  it("emits the option value when a different option is selected", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Select, {
      props: {
        options: objectOptions,
        modelValue: "default",
      },
    });

    await user.selectOptions(screen.getByRole("combobox"), "date");

    expect(emitted("update:modelValue")).toContainEqual(["date"]);
  });

  it("responds to external model changes", async () => {
    const { rerender } = render(Select, {
      props: { options: ["25", "50"], modelValue: "25" },
    });

    expect(screen.getByRole("combobox")).toHaveValue("25");

    await rerender({ modelValue: "50" });

    expect(screen.getByRole("combobox")).toHaveValue("50");
  });

  it("renders the placeholder as a disabled option", () => {
    render(Select, {
      props: { options: ["25"], placeholder: "Bitte auswählen" },
    });

    const placeholder = screen.getByRole("option", {
      name: "Bitte auswählen",
    });
    expect(placeholder).toBeDisabled();
    expect(placeholder).toHaveValue("");
  });

  it("renders no placeholder option when none is given", () => {
    render(Select, { props: { options: ["25", "50"] } });

    expect(screen.getAllByRole("option")).toHaveLength(2);
  });

  it("takes its accessible name from aria-labelledby", () => {
    render({
      components: { Select },
      template: `
        <div>
          <label id="sort-label">Sortieren nach</label>
          <Select :options="['25']" aria-labelledby="sort-label" />
        </div>
      `,
    });

    expect(
      screen.getByRole("combobox", { name: "Sortieren nach" }),
    ).toBeInTheDocument();
  });

  it("forwards id to the select so an external label can target it", () => {
    render({
      components: { Select },
      template: `
        <div>
          <label for="sort">Sortieren nach</label>
          <Select :options="['25']" id="sort" />
        </div>
      `,
    });

    expect(
      screen.getByRole("combobox", { name: "Sortieren nach" }),
    ).toBeInTheDocument();
  });

  it("forwards aria-invalid to the select", () => {
    render(Select, {
      props: { options: ["25"] },
      attrs: { "aria-invalid": "true" },
    });

    expect(screen.getByRole("combobox")).toHaveAttribute(
      "aria-invalid",
      "true",
    );
  });

  it("is not marked invalid by default", () => {
    render(Select, { props: { options: ["25"] } });

    expect(screen.getByRole("combobox")).not.toHaveAttribute("aria-invalid");
  });

  it("forwards aria-describedby so a hint can describe the select", () => {
    render({
      components: { Select },
      template: `
        <div>
          <Select
            :options="['25']"
            aria-describedby="hint"
            aria-invalid="true"
            aria-label="Anzahl"
          />
          <small id="hint">Bitte wählen Sie eine Option aus</small>
        </div>
      `,
    });

    expect(
      screen.getByRole("combobox", {
        name: "Anzahl",
        description: "Bitte wählen Sie eine Option aus",
      }),
    ).toBeInTheDocument();
  });

  it("forwards disabled to the select", () => {
    render(Select, {
      props: { options: ["25"] },
      attrs: { disabled: true },
    });

    expect(screen.getByRole("combobox")).toBeDisabled();
  });

  it("applies class to the wrapper rather than the select", () => {
    const { container } = render(Select, {
      props: { options: ["25"] },
      attrs: { class: "custom-class" },
    });

    expect(container.firstElementChild).toHaveClass("custom-class");
    expect(screen.getByRole("combobox")).not.toHaveClass("custom-class");
  });

  it("updates the options when the options prop changes", async () => {
    const { rerender } = render(Select, {
      props: { options: ["25", "50"] },
    });

    expect(screen.getAllByRole("option")).toHaveLength(2);

    await rerender({ options: ["25", "50", "100"] });

    expect(screen.getAllByRole("option")).toHaveLength(3);
  });
});
