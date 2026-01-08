import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import ExternalLink from "./ExternalLink.vue";

describe("ExternalLink", () => {
  it("renders external link", async () => {
    await renderSuspended(ExternalLink, {
      props: { url: "https://example.com" },
      slots: { default: () => "Example " },
    });

    const link = screen.getByRole("link", {
      name: "Example (öffnet in einem neuen Tab)",
    });

    expect(link).toHaveAttribute(
      "href",
      expect.stringContaining("https://example.com"),
    );

    expect(link).toHaveAttribute("target", "_blank");
  });
});
