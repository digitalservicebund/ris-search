import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import VersionsTeaser from "./VersionsTeaser.vue";

describe("VersionsTeaser", () => {
  it("shows the teaser text", async () => {
    await renderSuspended(VersionsTeaser);
    expect(
      screen.getByRole("heading", {
        name: /Fassungen sind noch nicht verfügbar/i,
      }),
    ).toBeInTheDocument();
  });
});
