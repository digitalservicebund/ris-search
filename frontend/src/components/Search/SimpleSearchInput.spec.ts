import userEvent from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import SimpleSearchInput from "./SimpleSearchInput.vue";

describe("SimpleSearchInput", () => {
  it("renders correctly", () => {
    render(SimpleSearchInput);

    expect(
      screen.getByRole("searchbox", { name: "Suchbegriff" }),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Suchen" })).toBeInTheDocument();
  });

  it("enables input after Nuxt is ready", async () => {
    render(SimpleSearchInput);

    expect(
      screen.getByRole("searchbox", { name: "Suchbegriff" }),
    ).not.toBeDisabled();
  });

  it("updates currentText, but not model on input change", async () => {
    const user = userEvent.setup();

    const { emitted } = render(SimpleSearchInput);

    await user.type(
      screen.getByRole("searchbox", { name: "Suchbegriff" }),
      "test query",
    );

    expect(screen.getByRole("searchbox", { name: "Suchbegriff" })).toHaveValue(
      "test query",
    );
    expect(emitted("update:modelValue")).toBeFalsy();
  });

  it("updates input on model change", async () => {
    render(SimpleSearchInput, {
      props: { modelValue: "updated model" },
    });

    expect(screen.getByRole("searchbox", { name: "Suchbegriff" })).toHaveValue(
      "updated model",
    );
  });

  it("submits search on Enter key press", async () => {
    const user = userEvent.setup();

    const { emitted } = render(SimpleSearchInput);

    const input = screen.getByRole("searchbox", { name: "Suchbegriff" });
    await user.type(input, "test query");
    await user.type(input, "{enter}");

    expect(emitted("update:modelValue")).toEqual([["test query"]]);
  });

  it("submits search on form submit", async () => {
    const user = userEvent.setup();

    const { emitted } = render(SimpleSearchInput);

    await user.type(
      screen.getByRole("searchbox", { name: "Suchbegriff" }),
      "test query",
    );
    await user.click(screen.getByRole("button", { name: "Suchen" }));

    expect(emitted("update:modelValue")).toEqual([["test query"]]);
  });

  it("allows customizing labels via props", () => {
    render(SimpleSearchInput, {
      props: {
        inputLabel: "Custom Input Label",
        inputPlaceholder: "Custom placeholder text",
        submitLabel: "Custom Submit",
      },
    });

    const input = screen.getByRole("searchbox", { name: "Custom Input Label" });
    expect(input).toBeInTheDocument();
    expect(input).toHaveAttribute("placeholder", "Custom placeholder text");
    expect(
      screen.getByRole("button", { name: "Custom Submit" }),
    ).toBeInTheDocument();
  });
});
