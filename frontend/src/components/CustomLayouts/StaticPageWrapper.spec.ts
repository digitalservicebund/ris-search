import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen, within } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import StaticPageWrapper from "./StaticPageWrapper.vue";

describe("StaticPageWrapper", () => {
  it("renders breadcrumb slot and content with correct semantics", async () => {
    await renderSuspended(StaticPageWrapper, {
      slots: {
        breadcrumb: "<ol><li>Home</li></ol>",
        default: "Main content",
      },
    });

    expect(
      screen.getByRole("heading", { level: 2, name: "Brotkrumen" }),
    ).toBeInTheDocument();

    const nav = screen.getByRole("navigation");
    expect(nav).toHaveAttribute("aria-labelledby", "breadcrumb-heading");
    expect(within(nav).getByText("Home")).toBeInTheDocument();

    expect(screen.getByText("Main content")).toBeInTheDocument();
  });
});
