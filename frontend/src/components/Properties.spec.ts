import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Properties from "./Properties.vue";

describe("Properties", () => {
  it("renders content", async () => {
    await renderSuspended(Properties, { slots: { default: "Info" } });
    expect(screen.getByText("Info")).toBeInTheDocument();
  });
});
