import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import NormLayout from "./norm.vue";

describe("norm", () => {
  it("renders slot content", async () => {
    await renderSuspended(NormLayout, {
      slots: { default: "<p>Page content</p>" },
    });
    expect(screen.getByText("Page content")).toBeInTheDocument();
  });
});
