import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import SkipLink from "./SkipLink.vue";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({ path: "/", query: {}, hash: "" })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

describe("SkipLink", () => {
  it("focuses the target element when activated", async () => {
    const user = userEvent.setup();

    await renderSuspended(SkipLink, {
      props: { to: "#target" },
      slots: { default: () => "Zum Inhalt springen" },
    });

    const target = document.createElement("main");
    target.id = "target";
    document.body.append(target);

    await user.click(screen.getByRole("link", { name: "Zum Inhalt springen" }));

    expect(target).toHaveFocus();
    expect(target).toHaveAttribute("tabindex", "-1");
    expect(target.className).toContain("skipLinkTarget");

    target.remove();
  });

  it("renders a link with the given hash and retains the current route query", async () => {
    useRouteMock.mockReturnValue({
      path: "/suche",
      query: { query: "test", documentKind: "R" },
      hash: "",
    });

    await renderSuspended(SkipLink, {
      props: { to: "#main-content" },
      slots: { default: () => "Zum Inhalt springen" },
    });

    const link = screen.getByRole("link", { name: "Zum Inhalt springen" });
    expect(link).toHaveAttribute(
      "href",
      expect.stringContaining("#main-content"),
    );
    expect(link).toHaveAttribute("href", expect.stringContaining("query=test"));
    expect(link).toHaveAttribute(
      "href",
      expect.stringContaining("documentKind=R"),
    );
  });
});
