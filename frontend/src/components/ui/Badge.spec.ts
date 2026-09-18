import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Badge from "./Badge.vue";

describe("Badge", () => {
  it("renders the label as plain text by default", () => {
    render(Badge, {
      props: {
        label: "Test Label",
        color: "blue",
      },
    });

    expect(screen.getByText("Test Label")).toBeInTheDocument();
  });

  it("renders content as html when 'isMarkup' is true", () => {
    const { container } = render(Badge, {
      props: {
        label: "<mark>Highlighted Content</mark>",
        color: "gray",
        isMarkup: true,
      },
    });
    expect(container.querySelector("mark")).toHaveTextContent(
      "Highlighted Content",
    );
  });

  it("sanitizes html content", () => {
    const { container } = render(Badge, {
      props: {
        label: "<mark><script>CSS</script>Highlighted Content</mark>",
        color: "gray",
        isMarkup: true,
      },
    });

    expect(container.querySelectorAll("script")).toHaveLength(0);
    expect(container.querySelector("mark")).toHaveTextContent(
      "Highlighted Content",
    );
  });

  it("applies blue styling for blue color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Blue Badge",
        color: "blue",
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("bg-blue-200");
  });

  it("applies green styling for green color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Green Badge",
        color: "green",
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("bg-green-100");
  });

  it("applies yellow styling for yellow color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Yellow Badge",
        color: "yellow",
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("bg-yellow-200");
  });

  it("applies red styling for red color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Red Badge",
        color: "red",
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("bg-red-200");
  });

  it("applies gray styling for gray color", () => {
    const { container } = render(Badge, {
      props: {
        label: "Gray Badge",
        color: "gray",
      },
    });

    const badge = container.firstChild as HTMLElement;
    expect(badge).toHaveClass("border-gray-400");
  });
});
