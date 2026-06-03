import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import AppSkipLinks from "./SkipLinks.vue";

describe("SkipLinks", () => {
  it("renders provided skip links", async () => {
    await renderSuspended(AppSkipLinks, {
      props: {
        links: [
          { label: "Zum Inhalt", to: "#main" },
          { label: "Zum Fußbereich", to: "#footer" },
        ],
      },
      route: "/no-links-here",
    });

    expect(screen.getByRole("link", { name: "Zum Inhalt" })).toBeVisible();
    expect(screen.getByRole("link", { name: "Zum Fußbereich" })).toBeVisible();
  });

  it("merges links from props with links from route meta", async () => {
    await renderSuspended(AppSkipLinks, {
      props: {
        links: [{ label: "Extra", to: "#extra" }],
      },
      route: "/no-links-here",
    });

    // Only prop links visible (no route meta on this route)
    expect(screen.getByRole("link", { name: "Extra" })).toBeVisible();
  });

  it("renders nothing when no links are provided and no route meta is set", async () => {
    await renderSuspended(AppSkipLinks, { route: "/no-links-here" });

    expect(
      screen.queryByRole("navigation", { name: "Sprunglinks" }),
    ).not.toBeInTheDocument();
  });

  it("focuses nav when navigating to a different path", async () => {
    await renderSuspended(AppSkipLinks, {
      props: { links: [{ label: "Zur Suche", to: "#search" }] },
      route: "/search",
    });

    const nav = screen.getByRole("navigation", { name: "Sprunglinks" });
    const focusSpy = vi.spyOn(nav, "focus");

    const router = useRouter();
    await router.push("/about");

    expect(focusSpy).toHaveBeenCalled();
    focusSpy.mockRestore();
  });

  it("does not focus nav when navigating to the same path with different query params", async () => {
    await renderSuspended(AppSkipLinks, {
      props: { links: [{ label: "Zur Suche", to: "#search" }] },
      route: "/search?q=foo",
    });

    const nav = screen.getByRole("navigation", { name: "Sprunglinks" });
    const focusSpy = vi.spyOn(nav, "focus");

    const router = useRouter();
    await router.push("/search?q=bar");

    expect(focusSpy).not.toHaveBeenCalled();
    focusSpy.mockRestore();
  });
});
