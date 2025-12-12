import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import DocsLayout from "./docs.vue";

describe("docs", () => {
  it("renders slot content", async () => {
    await renderSuspended(DocsLayout, {
      slots: { default: "<p>Documentation content</p>" },
    });
    expect(screen.getByText("Documentation content")).toBeInTheDocument();
  });
});
