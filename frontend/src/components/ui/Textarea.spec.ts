import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Textarea from "./Textarea.vue";

describe("Textarea", () => {
  it("renders a textbox", () => {
    render(Textarea);

    expect(screen.getByRole("textbox")).toBeInTheDocument();
  });

  it("reflects the model value", () => {
    render(Textarea, { props: { modelValue: "Hallo" } });

    expect(screen.getByRole("textbox")).toHaveValue("Hallo");
  });

  it("updates the model when the user types", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Textarea, { props: { modelValue: "" } });

    await user.type(screen.getByRole("textbox"), "abc");

    const updates = emitted("update:modelValue") as unknown[][];
    expect(updates.at(-1)?.[0]).toBe("abc");
  });

  it("passes through native attributes", () => {
    render(Textarea, {
      attrs: { placeholder: "Feedback", "aria-label": "Feedbackfeld" },
    });

    const textarea = screen.getByRole("textbox", { name: "Feedbackfeld" });
    expect(textarea).toHaveAttribute("placeholder", "Feedback");
  });

  it("reflects the disabled attribute", () => {
    render(Textarea, { attrs: { disabled: true } });

    expect(screen.getByRole("textbox")).toBeDisabled();
  });

  it("reflects the aria-invalid attribute", () => {
    render(Textarea, { attrs: { "aria-invalid": "true" } });

    expect(screen.getByRole("textbox")).toHaveAttribute("aria-invalid", "true");
  });
});
