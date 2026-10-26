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
      route: "/example",
    });

    expect(screen.getByRole("link", { name: "Zum Inhalt" })).toBeVisible();
    expect(screen.getByRole("link", { name: "Zum Fußbereich" })).toBeVisible();
  });

  it("merges links from props with links from route meta", async () => {
    await renderSuspended(AppSkipLinks, {
      props: {
        links: [{ label: "Target 1", to: "#target1" }],
      },
      route: "/example",
    });

    const router = useRouter();
    router.addRoute({
      path: "/with-meta",
      meta: { skipLinks: [{ label: "Target 2", to: "#target2" }] },
      component: {},
    });
    await router.push("/with-meta");

    expect(screen.getByRole("link", { name: "Target 1" })).toBeVisible();
    expect(screen.getByRole("link", { name: "Target 2" })).toBeVisible();
  });

  it("renders nothing when no links are provided and no route meta is set", async () => {
    await renderSuspended(AppSkipLinks, { route: "/example" });

    expect(
      screen.queryByRole("navigation", { name: "Sprunglinks" }),
    ).not.toBeInTheDocument();
  });

  it("focuses nav when navigating to a different path", async () => {
    await renderSuspended(AppSkipLinks, {
      props: { links: [{ label: "Zur Suche", to: "#search" }] },
      route: "/suche",
    });

    const nav = screen.getByRole("navigation", { name: "Sprunglinks" });
    const focusSpy = vi.spyOn(nav, "focus");

    const router = useRouter();
    await router.push("/ueber");

    expect(focusSpy).toHaveBeenCalled();
    focusSpy.mockRestore();
  });

  it("does not focus nav when navigating to the same path with different query params", async () => {
    await renderSuspended(AppSkipLinks, {
      props: { links: [{ label: "Zur Suche", to: "#search" }] },
      route: "/suche?q=foo",
    });

    const nav = screen.getByRole("navigation", { name: "Sprunglinks" });
    const focusSpy = vi.spyOn(nav, "focus");

    const router = useRouter();
    await router.push("/suche?q=bar");

    expect(focusSpy).not.toHaveBeenCalled();
    focusSpy.mockRestore();
  });
});
