import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import RadioButton from "./RadioButton.vue";

describe("RadioButton", () => {
  it("renders a native radio input", () => {
    render(RadioButton, { props: { value: "a" } });

    expect(screen.getByRole("radio")).toBeInTheDocument();
  });

  it("reflects the value prop on the input", () => {
    render(RadioButton, { props: { value: "urteil" } });

    expect(screen.getByRole("radio")).toHaveAttribute("value", "urteil");
  });

  it("is checked when the model matches its value", () => {
    render(RadioButton, { props: { value: "a", modelValue: "a" } });

    expect(screen.getByRole("radio")).toBeChecked();
  });

  it("is not checked when the model differs from its value", () => {
    render(RadioButton, { props: { value: "a", modelValue: "b" } });

    expect(screen.getByRole("radio")).not.toBeChecked();
  });

  it("emits its value when selected", async () => {
    const user = userEvent.setup();
    const { emitted } = render(RadioButton, {
      props: { value: "period", modelValue: "allTime" },
    });

    await user.click(screen.getByRole("radio"));

    expect(emitted("update:modelValue")).toContainEqual(["period"]);
  });

  it("responds to external model changes", async () => {
    const { rerender } = render(RadioButton, {
      props: { value: "a", modelValue: "b" },
    });

    expect(screen.getByRole("radio")).not.toBeChecked();

    await rerender({ modelValue: "a" });

    expect(screen.getByRole("radio")).toBeChecked();
  });

  it("forwards id and name to the input so an external label can target it", () => {
    render(RadioButton, {
      props: { value: "a" },
      attrs: { id: "filter-a", name: "filter" },
    });

    const radio = screen.getByRole("radio");
    expect(radio).toHaveAttribute("id", "filter-a");
    expect(radio).toHaveAttribute("name", "filter");
  });

  it("forwards disabled to the input and does not emit when clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = render(RadioButton, {
      props: { value: "a" },
      attrs: { disabled: true },
    });

    expect(screen.getByRole("radio")).toBeDisabled();

    await user.click(screen.getByRole("radio"));

    expect(emitted()).not.toHaveProperty("update:modelValue");
  });

  it("forwards aria attributes to the input", () => {
    render(RadioButton, {
      props: { value: "a" },
      attrs: { "aria-invalid": "true", "aria-label": "Zeitraum" },
    });

    expect(screen.getByRole("radio", { name: "Zeitraum" })).toHaveAttribute(
      "aria-invalid",
      "true",
    );
  });

  it("applies class to the wrapper rather than the input", () => {
    const { container } = render(RadioButton, {
      props: { value: "a" },
      attrs: { class: "custom-class" },
    });

    expect(container.firstElementChild).toHaveClass("custom-class");
    expect(screen.getByRole("radio")).not.toHaveClass("custom-class");
  });

  it("only selects the clicked radio within a group", async () => {
    const user = userEvent.setup();
    render({
      components: { RadioButton },
      template: `
        <div>
          <RadioButton v-model="selected" id="one" name="group" value="one" />
          <label for="one">One</label>
          <RadioButton v-model="selected" id="two" name="group" value="two" />
          <label for="two">Two</label>
        </div>
      `,
      data: () => ({ selected: "one" }),
    });

    expect(screen.getByRole("radio", { name: "One" })).toBeChecked();

    await user.click(screen.getByRole("radio", { name: "Two" }));

    expect(screen.getByRole("radio", { name: "Two" })).toBeChecked();
    expect(screen.getByRole("radio", { name: "One" })).not.toBeChecked();
  });
});
