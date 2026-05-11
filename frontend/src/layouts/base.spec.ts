import { renderSuspended, mockNuxtImport } from "@nuxt/test-utils/runtime";
import { screen, waitFor } from "@testing-library/vue";
import { defineComponent, h, reactive } from "vue";
import { describe, expect, it, vi } from "vitest";
import BaseLayout from "./base.vue";

const route = reactive({ path: "/page-a", fullPath: "/page-a", query: {} });

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(),
}));

mockNuxtImport("useRoute", () => useRouteMock);

describe("base", () => {
  it("renders slot content", async () => {
    useRouteMock.mockReturnValue(route);

    await renderSuspended(BaseLayout, {
      slots: { default: () => "Slot content" },
    });

    expect(screen.getByText("Slot content")).toBeVisible();
  });

  describe("skip links", () => {
    const PageA = defineComponent({
      setup() {
        useSkipLinks([
          { label: "Zum Inhalt springen", to: "#content" },
          { label: "Zu den Filtern springen", to: "#filters" },
        ]);

        return () =>
          h("div", [
            h("main", { id: "content" }, "Inhalt"),
            h("aside", { id: "filters" }, "Filter"),
          ]);
      },
    });

    const PageB = defineComponent({
      setup() {
        useSkipLinks([{ label: "Zu den Details springen", to: "#details" }]);
        return () => h("section", { id: "details" }, "Details");
      },
    });

    const SkipLinksWrapper = defineComponent({
      setup() {
        return () =>
          h(BaseLayout, null, {
            default: () => h(route.fullPath === "/page-a" ? PageA : PageB),
          });
      },
    });

    it("throws away registered skip links on navigation", async () => {
      route.fullPath = "/page-a";
      route.path = "/page-a";
      useRouteMock.mockReturnValue(route);

      await renderSuspended(SkipLinksWrapper, {
        global: {
          stubs: {
            AppHeader: true,
            AppFooter: true,
            ConsentBanner: true,
          },
        },
      });

      route.path = "/page-b";
      route.fullPath = "/page-b";

      await waitFor(() => {
        expect(
          screen.queryByRole("link", { name: "Zum Inhalt springen" }),
        ).not.toBeInTheDocument();
        expect(
          screen.getByRole("link", { name: "Zu den Details springen" }),
        ).toBeInTheDocument();
      });
    });

    it("keeps registered skip links on hash-only navigation", async () => {
      route.path = "/page-a";
      route.fullPath = "/page-a";
      useRouteMock.mockReturnValue(route);

      await renderSuspended(SkipLinksWrapper, {
        global: {
          stubs: {
            AppHeader: true,
            AppFooter: true,
            ConsentBanner: true,
          },
        },
      });

      route.fullPath = "/page-a#content";

      await waitFor(() => {
        expect(
          screen.getByRole("link", { name: "Zum Inhalt springen" }),
        ).toBeInTheDocument();
        expect(
          screen.getByRole("link", { name: "Zu den Filtern springen" }),
        ).toBeInTheDocument();
      });
    });
  });
});
