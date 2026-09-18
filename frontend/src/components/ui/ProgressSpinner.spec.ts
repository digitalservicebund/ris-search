import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import ProgressSpinner from "./ProgressSpinner.vue";

describe("ProgressSpinner", () => {
  it("renders with an accessible label", () => {
    render(ProgressSpinner);

    const spinner = screen.getByLabelText("Ladestatus");
    expect(spinner).toBeInTheDocument();
  });

  it("exposes a status role", () => {
    render(ProgressSpinner);

    expect(screen.getByRole("status")).toBeInTheDocument();
  });

  it("spins", () => {
    render(ProgressSpinner);

    expect(screen.getByRole("status")).toHaveClass("animate-spin");
  });
});
