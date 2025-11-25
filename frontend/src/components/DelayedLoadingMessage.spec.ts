import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import DelayedLoadingMessage from "./DelayedLoadingMessage.vue";

describe("DelayedLoadingMessage", () => {
  it("renders loading message", async () => {
    await renderSuspended(DelayedLoadingMessage, {
      slots: { default: "Bitte warten" },
    });

    expect(screen.getByLabelText("Ladestatus")).toBeInTheDocument();
    expect(screen.getByText("Bitte warten")).toBeInTheDocument();
  });
});
