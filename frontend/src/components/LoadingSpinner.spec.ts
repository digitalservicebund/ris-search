import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import LoadingSpinner from "./LoadingSpinner.vue";

describe("LoadingSpinner", () => {
  it("renders", async () => {
    await renderSuspended(LoadingSpinner);
    const spinner = screen.getByLabelText("Ladestatus");
    expect(spinner).toBeInTheDocument();
  });
});
