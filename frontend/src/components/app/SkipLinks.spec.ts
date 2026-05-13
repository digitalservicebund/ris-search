import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { defineComponent, h } from "vue";
import { describe, expect, it } from "vitest";
import AppSkipLinks from "./SkipLinks.vue";

const RegisterSkipLinks = defineComponent({
  setup() {
    useSkipLinks([
      { label: "Zum Inhalt", to: "#main" },
      { label: "Zum Fußbereich", to: "#footer" },
    ]);

    return () => null;
  },
});

const SkipLinksDummy = defineComponent({
  setup() {
    provideSkipLinks();
    return () => [h(AppSkipLinks), h(RegisterSkipLinks)];
  },
});

describe("SkipLinks", () => {
  it("renders registered skip links", async () => {
    await renderSuspended(SkipLinksDummy);

    expect(screen.getByRole("link", { name: "Zum Inhalt" })).toBeVisible();
    expect(screen.getByRole("link", { name: "Zum Fußbereich" })).toBeVisible();
  });
});
