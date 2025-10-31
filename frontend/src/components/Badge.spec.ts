import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Badge, { BadgeColor } from "./Badge.vue";

describe("Badge", () => {
  it("renders the label text", () => {
    render(Badge, {
      props: {
        label: "Test Label",
        color: BadgeColor.BLUE,
      },
    });

    expect(screen.getByText("Test Label")).toBeInTheDocument();
  });

  it("applies blue styling for BLUE color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Blue Badge",
        color: BadgeColor.BLUE,
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("bg-blue-200");
  });

  it("applies green styling for GREEN color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Green Badge",
        color: BadgeColor.GREEN,
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("bg-green-100");
  });

  it("applies yellow styling for YELLOW color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Yellow Badge",
        color: BadgeColor.YELLOW,
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("bg-yellow-200");
  });

  it("applies red styling for RED color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Red Badge",
        color: BadgeColor.RED,
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("bg-red-200");
  });
});
