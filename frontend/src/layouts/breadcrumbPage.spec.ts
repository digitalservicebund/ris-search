import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import BreadcrumbPageLayout from "./breadcrumbPage.vue";

describe("content", () => {
  it("renders default slot content", async () => {
    await renderSuspended(BreadcrumbPageLayout, {
      slots: { default: "<p>Page content</p>" },
    });
    expect(screen.getByText("Page content")).toBeInTheDocument();
  });

  it("renders breadcrumb slot", async () => {
    await renderSuspended(BreadcrumbPageLayout, {
      slots: { breadcrumb: "<p>Breadcrumb</p>" },
    });
    expect(screen.getByText("Breadcrumb")).toBeInTheDocument();
  });
});
