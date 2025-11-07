import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import { describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import Pagination from "./Pagination.vue";
import type { Page } from "~/components/Pagination/Pagination.vue";

vi.mock("#components", () => ({
  Button: {
    name: "Button",
    props: ["to"],
    template: '<a :href="to?.path" data-test="nuxt-link"><slot /></a>',
  },
}));

const { useRouteMock } = vi.hoisted(() => {
  return {
    useRouteMock: vi.fn(() => {
      return { path: "/test-path", query: {} };
    }),
  };
});

mockNuxtImport("useRoute", () => {
  return useRouteMock;
});

const basePage = {
  "@id": "/api/results?pageIndex=0",
  member: [{}],
  totalItems: 10,
  view: {},
} as Page;

async function mountWithPage(page: Page = basePage) {
  return await mountSuspended(Pagination, {
    props: { page },
    stubs: {
      Button: {
        name: "Button",
        props: ["to"],
        template: '<a :href="to?.path" data-test="nuxt-link"><slot /></a>',
      },
    },
  });
}

describe("Pagination.vue", () => {
  it("renders nothing if no members", async () => {
    const wrapper = await mountWithPage({ ...basePage, member: [] });
    expect(wrapper.html()).not.toContain("Zurück");
    expect(wrapper.html()).not.toContain("Weiter");
  });

  it("renders disabled span buttons when only one page", async () => {
    useRouteMock.mockReturnValue({
      path: "test-path",
      query: { pageNumber: 1 },
    });
    const wrapper = await mountWithPage();
    const disabledSpans = wrapper.findAll('span[aria-disabled="true"]');
    expect(disabledSpans.length).toBe(0);
  });

  it("renders NuxtLink buttons for both directions when multiple pages exist", async () => {
    const wrapper = await mountWithPage({
      ...basePage,
      view: {
        previous: "/api/results?pageIndex=0",
        next: "/api/results?pageIndex=2",
      },
    });

    const links = wrapper.findAllComponents({ name: "Button" });
    expect(links.length).toBe(2);
    expect(links[0]?.attributes("href")).toBe("/test-path");
    expect(links[1]?.attributes("href")).toBe("/test-path?pageNumber=2");
  });

  it("renders only next link if previous missing", async () => {
    const wrapper = await mountWithPage({
      ...basePage,
      view: { next: "/api/results?page=2" },
    });
    const nextLink = wrapper.find('[aria-label="nächste Ergebnisse"]');
    expect(nextLink.exists()).toBe(true);
    expect(nextLink.text()).toContain("Weiter");
    expect(wrapper.text()).toContain("Seite");
  });

  it("renders only previous link if next missing", async () => {
    const wrapper = await mountWithPage({
      ...basePage,
      view: { previous: "/api/results?pageIndex=0" },
    });
    const prevLink = wrapper.find('[aria-label="vorherige Ergebnisse"]');
    expect(prevLink.exists()).toBe(true);
    expect(prevLink.text()).toContain("Zurück");
  });

  it("emits updatePage with correct number when clicking next", async () => {
    const wrapper = await mountWithPage({
      ...basePage,
      view: { next: "/api/results?pageIndex=2" },
    });
    await nextTick();
    await wrapper.find('[aria-label="nächste Ergebnisse"]').trigger("click");
    expect(wrapper.emitted("updatePage")).toBeTruthy();
    expect(wrapper.emitted("updatePage")![0]![0]).toBe(2);
  });

  it("emits updatePage with correct number when clicking previous", async () => {
    const wrapper = await mountWithPage({
      ...basePage,
      view: { previous: "/api/results?pageIndex=0" },
    });
    await wrapper.find('[aria-label="vorherige Ergebnisse"]').trigger("click");
    expect(wrapper.emitted("updatePage")).toBeTruthy();
    expect(wrapper.emitted("updatePage")![0]![0]).toBe(0);
  });

  it("renders nothing if isLoading is true", async () => {
    const page = {
      ...basePage,
      view: {
        previous: "/api/results?pageIndex=0",
        next: "/api/results?pageIndex=2",
      },
    };
    const wrapper = await mountSuspended(Pagination, {
      props: { page, isLoading: true },
    });
    expect(wrapper.html()).not.toContain("Zurück");
    expect(wrapper.html()).not.toContain("Weiter");
  });

  it("displays navigation position correctly (top/bottom slots)", async () => {
    const wrapperTop = await mountSuspended(Pagination, {
      props: { page: basePage, navigationPosition: "top" },
      slots: { default: '<div data-test="slot">SlotContent</div>' },
    });
    const wrapperBottom = await mountSuspended(Pagination, {
      props: { page: basePage, navigationPosition: "bottom" },
      slots: { default: '<div data-test="slot">SlotContent</div>' },
    });
    expect(wrapperTop.find('[data-test="slot"]').exists()).toBe(true);
    expect(wrapperBottom.find('[data-test="slot"]').exists()).toBe(true);
  });
});
