import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen, within } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import StaticPage from "./staticPage.vue";

describe("staticPage", () => {
  it("renders breadcrumb slot and content with correct semantics", async () => {
    await renderSuspended(StaticPage, {
      slots: {
        breadcrumb: "<ol><li>Home</li></ol>",
        pageTitle: "Title",
        default: "Main content",
      },
    });

    expect(
      screen.getByRole("heading", { level: 2, name: "Brotkrumen" }),
    ).toBeInTheDocument();

    const nav = screen.getByRole("navigation", { name: "Brotkrumen" });
    expect(within(nav).getByText("Home")).toBeInTheDocument();

    expect(
      screen.getByRole("heading", { level: 1, name: "Title" }),
    ).toBeInTheDocument();

    expect(screen.getByText("Main content")).toBeInTheDocument();
  });
});
