import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { fireEvent, screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import Pagination from "./Pagination.vue";
import type { Page } from "~/components/Pagination/Pagination.vue";

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

interface Props {
  page?: Page | null;
  navigationPosition?: "top" | "bottom";
  isLoading?: boolean;
}

function renderComponent(props = { page: basePage } as Props, slots = {}) {
  return renderSuspended(Pagination, {
    props,
    slots,
    global: {
      stubs: {
        NuxtLink: {
          name: "NuxtLink",
          props: ["to"],
          template: `<a :href="to?.path + (to?.query?.pageNumber ? '?pageNumber=' + to?.query?.pageNumber : '')"><slot /></a>`,
        },
      },
    },
  });
}

describe("Pagination.vue", () => {
  it("renders nothing if no members", async () => {
    await renderComponent({ page: { ...basePage, member: [] } });
    expect(screen.queryByText("Zurück")).not.toBeInTheDocument();
    expect(screen.queryByText("Weiter")).not.toBeInTheDocument();
  });

  it("renders disabled span buttons when only one page", async () => {
    useRouteMock.mockReturnValue({
      path: "/test-path",
      query: { pageNumber: 1 },
    });
    await renderComponent({ page: basePage });
    const disabled = screen.queryAllByRole("button", { hidden: false });
    expect(disabled.length).toBe(0);
  });

  it("renders NuxtLink buttons for both directions when multiple pages exist", async () => {
    await renderComponent({
      page: {
        ...basePage,
        view: {
          previous: "/api/results?pageIndex=0",
          next: "/api/results?pageIndex=2",
        },
      },
    });

    const links = screen.getAllByRole("link");
    expect(links.length).toBe(2);
    expect(links[0]?.getAttribute("href")).toBe("/test-path");
    expect(links[1]?.getAttribute("href")).toBe("/test-path?pageNumber=2");
  });

  it("renders only next link if previous missing", async () => {
    await renderComponent({
      page: {
        ...basePage,
        view: { next: "/api/results?page=2" },
      },
    });

    expect(screen.getByLabelText("nächste Ergebnisse")).toBeInTheDocument();

    expect(screen.getByText("Weiter")).toBeInTheDocument();
    expect(screen.getByText(/Seite/)).toBeInTheDocument();
  });

  it("renders only previous link if next missing", async () => {
    await renderComponent({
      page: {
        ...basePage,
        view: { previous: "/api/results?pageIndex=0" },
      },
    });

    expect(screen.getByLabelText("vorherige Ergebnisse")).toBeInTheDocument();

    expect(screen.getByText("Zurück")).toBeInTheDocument();
  });

  it("emits updatePage with correct number when clicking next", async () => {
    const { emitted } = await renderComponent({
      page: {
        ...basePage,
        view: { next: "/api/results?pageIndex=2" },
      },
    });

    const nextBtn = screen.getByLabelText("nächste Ergebnisse");
    await fireEvent.click(nextBtn);

    expect(emitted().updatePage).toBeTruthy();
    const updatePageIndex = emitted().updatePage?.[0] as Array<number>;
    expect(updatePageIndex[0]).toBe(2);
  });

  it("emits updatePage with correct number when clicking previous", async () => {
    const { emitted } = await renderComponent({
      page: {
        ...basePage,
        view: { previous: "/api/results?pageIndex=0" },
      },
    });

    const prevBtn = screen.getByLabelText("vorherige Ergebnisse");
    await fireEvent.click(prevBtn);

    expect(emitted().updatePage).toBeTruthy();
    const updatePageIndex = emitted().updatePage?.[0] as Array<number>;
    expect(updatePageIndex[0]).toBe(0);
  });

  it("renders nothing if isLoading is true", async () => {
    await renderComponent({
      page: {
        ...basePage,
        view: {
          previous: "/api/results?pageIndex=0",
          next: "/api/results?pageIndex=2",
        },
      },
      isLoading: true,
    });

    expect(screen.queryByText("Zurück")).not.toBeInTheDocument();
    expect(screen.queryByText("Weiter")).not.toBeInTheDocument();
  });

  it("displays navigation position correctly (top/bottom slots)", async () => {
    await renderComponent(
      { page: basePage, navigationPosition: "top" },
      { default: '<div data-test="slot">SlotContent</div>' },
    );
    expect(screen.getAllByText("SlotContent").length).toBeGreaterThan(0);

    await renderComponent(
      { page: basePage, navigationPosition: "bottom" },
      { default: '<div data-test="slot">SlotContent</div>' },
    );
    expect(screen.getAllByText("SlotContent").length).toBeGreaterThan(0);
  });
});
