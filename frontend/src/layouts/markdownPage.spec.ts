import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen, within } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import MarkdownPage from "./markdownPage.vue";

describe("markdownPage", () => {
  it("renders breadcrumb slot", async () => {
    await renderSuspended(MarkdownPage, {
      props: {
        staticContent: "",
      },
      slots: {
        breadcrumb: "<ol><li>Home</li><li>Current</li></ol>",
      },
    });

    const nav = screen.getByRole("navigation", { name: "Brotkrumen" });
    expect(nav).toBeInTheDocument();
    expect(within(nav).getByText("Home")).toBeInTheDocument();
    expect(within(nav).getByText("Current")).toBeInTheDocument();
  });

  it("renders markdown content", async () => {
    const markdownContent = "# Hello World";

    await renderSuspended(MarkdownPage, {
      props: {
        staticContent: markdownContent,
      },
    });

    expect(
      screen.getByRole("heading", { level: 1, name: "Hello World" }),
    ).toBeInTheDocument();
  });
});
