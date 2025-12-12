import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import DocumentLayout from "./document.vue";

describe("document", () => {
  it("renders slot content", async () => {
    await renderSuspended(DocumentLayout, {
      slots: { default: "<p>Page content</p>" },
    });
    expect(screen.getByText("Page content")).toBeInTheDocument();
  });
});
