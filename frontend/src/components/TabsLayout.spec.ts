import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import { nextTick, reactive } from "vue";
import TabsLayout, { type TabView } from "./TabsLayout.vue";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({ query: {} })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

const IconA = { template: `<svg aria-label="icon-a" />` };
const IconB = { template: `<svg aria-label="icon-b" />` };

const views: OneOrMore<TabView> = [
  { label: "Tab A", path: "view-a", icon: IconA },
  { label: "Tab B", path: "view-b", icon: IconB, analyticsId: "tab-b" },
];

describe("TabsLayout", () => {
  it("renders all tab labels", async () => {
    useRouteMock.mockReturnValue({ query: {} });

    await renderSuspended(TabsLayout, { props: { views } });

    expect(screen.getByRole("tab", { name: /Tab A/i })).toBeInTheDocument();
    expect(screen.getByRole("tab", { name: /Tab B/i })).toBeInTheDocument();
  });

  it("renders tab icons", async () => {
    useRouteMock.mockReturnValue({ query: {} });

    await renderSuspended(TabsLayout, { props: { views } });

    expect(screen.getByLabelText("icon-a")).toBeInTheDocument();
    expect(screen.getByLabelText("icon-b")).toBeInTheDocument();
  });

  it("marks only the first tab as active when no query param is set", async () => {
    useRouteMock.mockReturnValue({ query: {} });

    await renderSuspended(TabsLayout, { props: { views } });

    expect(screen.getByRole("tab", { name: /Tab A/i })).toHaveAttribute(
      "aria-selected",
      "true",
    );
    expect(screen.getByRole("tab", { name: /Tab B/i })).toHaveAttribute(
      "aria-selected",
      "false",
    );
  });

  it("marks only the tab matching route.query.view as active", async () => {
    useRouteMock.mockReturnValue({ query: { view: "view-b" } });

    await renderSuspended(TabsLayout, { props: { views } });

    expect(screen.getByRole("tab", { name: /Tab A/i })).toHaveAttribute(
      "aria-selected",
      "false",
    );

    expect(screen.getByRole("tab", { name: /Tab B/i })).toHaveAttribute(
      "aria-selected",
      "true",
    );
  });

  it("renders the named slot for the active view", async () => {
    useRouteMock.mockReturnValue({ query: { view: "view-a" } });

    await renderSuspended(TabsLayout, {
      props: { views },
      slots: { "view-a": "Content A" },
    });

    expect(screen.getByText("Content A")).toBeInTheDocument();
  });

  it("does not render the slot for an inactive view", async () => {
    useRouteMock.mockReturnValue({ query: { view: "view-a" } });

    await renderSuspended(TabsLayout, {
      props: { views },
      slots: { "view-a": "Content A", "view-b": "Content B" },
    });

    expect(screen.queryByText("Content B")).not.toBeInTheDocument();
  });

  it("renders tab links with the correct view query param", async () => {
    useRouteMock.mockReturnValue({ query: {} });

    await renderSuspended(TabsLayout, { props: { views } });

    const tabA = screen.getByRole("tab", { name: /Tab A/i });
    const tabB = screen.getByRole("tab", { name: /Tab B/i });

    expect(tabA).toHaveAttribute(
      "href",
      expect.stringContaining("view=view-a"),
    );
    expect(tabB).toHaveAttribute(
      "href",
      expect.stringContaining("view=view-b"),
    );
  });

  it("forwards analyticsId as data-attr on the tab", async () => {
    useRouteMock.mockReturnValue({ query: {} });

    await renderSuspended(TabsLayout, { props: { views } });

    expect(screen.getByRole("tab", { name: /Tab B/i })).toHaveAttribute(
      "data-attr",
      "tab-b",
    );
  });

  it("updates active tab and displayed slot when route query changes", async () => {
    const route = reactive({ query: { view: "view-a" } });
    useRouteMock.mockReturnValue(route);

    await renderSuspended(TabsLayout, {
      props: { views },
      slots: { "view-a": "Content A", "view-b": "Content B" },
    });

    expect(screen.getByRole("tab", { name: /Tab A/i })).toHaveAttribute(
      "aria-selected",
      "true",
    );
    expect(screen.getByText("Content A")).toBeInTheDocument();
    expect(screen.queryByText("Content B")).not.toBeInTheDocument();

    route.query = { view: "view-b" };
    await nextTick();

    expect(screen.getByRole("tab", { name: /Tab B/i })).toHaveAttribute(
      "aria-selected",
      "true",
    );
    expect(screen.getByText("Content B")).toBeInTheDocument();
    expect(screen.queryByText("Content A")).not.toBeInTheDocument();
  });
});
