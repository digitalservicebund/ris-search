import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import IncompleteDataMessage from "./IncompleteDataMessage.vue";

describe("IncompleteDataMessage", () => {
  it("shows the trial notice", async () => {
    await renderSuspended(IncompleteDataMessage);
    expect(
      screen.getByText(/Dieser Service befindet sich in der Testphase/i),
    ).toBeInTheDocument();
  });
});
