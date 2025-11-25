import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Banner from "./Banner.vue";

describe("Banner", () => {
  it("shows the trial banner", async () => {
    await renderSuspended(Banner);
    expect(screen.getByText(/This is a trial service/i)).toBeInTheDocument();
  });
});
