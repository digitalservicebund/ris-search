import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { defineComponent, h } from "vue";
import { describe, expect, it, vi } from "vitest";
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

  it("focuses nav when navigating to a different path", async () => {
    await renderSuspended(SkipLinksDummy, { route: "/search" });
    const nav = screen.getByRole("navigation", { name: "Sprunglinks" });
    const focusSpy = vi.spyOn(nav, "focus");

    const router = useRouter();
    await router.push("/about");

    expect(focusSpy).toHaveBeenCalled();
    focusSpy.mockRestore();
  });

  it("does not focus nav when navigating to the same path with different query params", async () => {
    await renderSuspended(SkipLinksDummy, { route: "/search?q=foo" });
    const nav = screen.getByRole("navigation", { name: "Sprunglinks" });
    const focusSpy = vi.spyOn(nav, "focus");

    const router = useRouter();
    await router.push("/search?q=bar");

    expect(focusSpy).not.toHaveBeenCalled();
    focusSpy.mockRestore();
  });
});
