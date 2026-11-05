import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import LegislationContent from "./LegislationContent.vue";

const { mockNavigateTo } = vi.hoisted(() => ({ mockNavigateTo: vi.fn() }));
mockNuxtImport("navigateTo", () => mockNavigateTo);

describe("LegislationContent", () => {
  it("navigates with the router for links in the content", async () => {
    const normPath = "/gesetze/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";
    render(LegislationContent, {
      slots: {
        default: `<a href="${normPath}/art-z1">§ 1</a>`,
      },
    });

    await userEvent.click(screen.getByRole("link", { name: "§ 1" }));

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith(
      `${normPath}/art-z1`,
    );
  });

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
