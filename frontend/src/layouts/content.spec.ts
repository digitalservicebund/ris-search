import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import ContentLayout from "./content.vue";

describe("content", () => {
  it("renders default slot content", async () => {
    await renderSuspended(ContentLayout, {
      slots: { default: "<p>Page content</p>" },
    });
    expect(screen.getByText("Page content")).toBeInTheDocument();
  });

  it("renders breadcrumb slot", async () => {
    await renderSuspended(ContentLayout, {
      slots: { breadcrumb: "<p>Breadcrumb</p>" },
    });
    expect(screen.getByText("Breadcrumb")).toBeInTheDocument();
  });
});
