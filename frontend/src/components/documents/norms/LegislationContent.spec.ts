import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import LegislationContent from "./LegislationContent.vue";

describe("LegislationContent", () => {
  it("renders slot content", () => {
    render(LegislationContent, {
      slots: {
        default: "<div>Test content</div>",
      },
    });

    expect(screen.getByText("Test content")).toBeInTheDocument();
    expect(
      screen.queryByText("Amtliches Inhaltsverzeichnis"),
    ).not.toBeInTheDocument();
  });

  it("renders slot content and official Toc", () => {
    render(LegislationContent, {
      slots: {
        default: "<div>Test content</div>",
      },
      props: {
        officialToc: "<div>officialToc</div>",
      },
    });

    expect(screen.getByText("Test content")).toBeInTheDocument();
    expect(screen.getByText("officialToc")).toBeInTheDocument();
  });

  it("applies single-article class", () => {
    const { container } = render(LegislationContent, {
      props: {
        singleArticle: true,
      },
    });

    const wrapper = container.querySelector(".legislation");
    expect(wrapper).toHaveClass("single-article");
  });

  it("does not apply single-article class", () => {
    const { container } = render(LegislationContent, {
      props: {
        singleArticle: false,
      },
    });

    const wrapper = container.querySelector(".legislation");
    expect(wrapper).not.toHaveClass("single-article");
  });
});
