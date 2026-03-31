import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import SidebarLayout from "./SidebarLayout.vue";

describe("SidebarLayout", () => {
  it("renders content and sidebar", async () => {
    await renderSuspended(SidebarLayout, {
      slots: {
        content: "Main content",
        sidebar: "Sidebar content",
      },
    });

    expect(screen.getByText("Main content")).toBeInTheDocument();
    expect(screen.getByText("Sidebar content")).toBeInTheDocument();
  });

  it("does not render the sidebar container when the sidebar slot is empty", async () => {
    const { container } = await renderSuspended(SidebarLayout, {
      slots: {
        content: "Main content",
      },
    });

    expect(screen.getByText("Main content")).toBeInTheDocument();
    expect(container.querySelector(".border-l.border-gray-400")).toBeNull();
  });
});
