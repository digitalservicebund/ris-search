import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import DocumentLayout, { type DocumentView } from "./document.vue";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({ query: {} })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

const defaultViews: OneOrMore<DocumentView> = [
  { label: "Text", path: "text", icon: IcBaselineSubject },
  { label: "Details", path: "details", icon: IcOutlineInfo },
];

describe("document", () => {
  it("renders title", async () => {
    await renderSuspended(DocumentLayout, {
      props: {
        title: "Title",
        titlePlaceholder: "Title Placeholder",
        views: defaultViews,
      },
    });

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Title",
    );
  });

  it("renders title placeholder if no title provided", async () => {
    await renderSuspended(DocumentLayout, {
      props: {
        titlePlaceholder: "Title Placeholder",
        views: defaultViews,
      },
    });

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Title Placeholder",
    );
  });

  it("renders breadcrumbs", async () => {
    await renderSuspended(DocumentLayout, {
      props: {
        titlePlaceholder: "Title Placeholder",
        breadcrumbs: [
          { label: "Breadcrumb 1", route: "/someRoute" },
          { label: "Breadcrumb 2" },
        ],
        views: defaultViews,
      },
      global: {
        stubs: {
          NuxtLink: {
            template: '<a :href="to"><slot /></a>',
            props: ["to"],
          },
        },
      },
    });

    expect(screen.getByRole("link", { name: "Breadcrumb 1" })).toBeVisible();
    expect(screen.getByText("Breadcrumb 2")).toBeVisible();
  });

  it("renders metadata items", async () => {
    await renderSuspended(DocumentLayout, {
      props: {
        titlePlaceholder: "Title Placeholder",
        metadata: [
          { label: "Label 1", value: "Value 1" },
          { label: "Label 2" },
        ],
        views: defaultViews,
      },
    });

    const terms = screen.getAllByRole("term");
    expect(terms[0]).toHaveTextContent("Label 1");
    expect(terms[0]?.nextElementSibling).toHaveTextContent("Value 1");

    expect(terms[1]).toHaveTextContent("Label 2");
    expect(terms[1]?.nextElementSibling).toHaveTextContent("—");
  });

  it("renders slots for views", async () => {
    await renderSuspended(DocumentLayout, {
      props: {
        title: "Title",
        titlePlaceholder: "Title Placeholder",
        views: defaultViews,
      },
      slots: {
        actionMenu: () => "ActionMenu",
        text: () => "Text Content",
        details: () => "Details Content",
      },
    });

    expect(screen.getByText("ActionMenu")).toBeVisible();
    expect(screen.getByText("Text Content")).toBeVisible();
  });

  it("renders details slot for empty documents", async () => {
    await renderSuspended(DocumentLayout, {
      props: {
        title: "Title",
        titlePlaceholder: "Title Placeholder",
        isEmptyDocument: true,
        views: defaultViews,
      },
      slots: {
        details: () => "Empty Document Details",
      },
    });

    expect(screen.getByText("Empty Document Details")).toBeVisible();
    // Tabs should not be rendered for empty documents
    expect(screen.queryByRole("tablist")).not.toBeInTheDocument();
  });

  it("renders tabs for non-empty documents", async () => {
    await renderSuspended(DocumentLayout, {
      props: {
        title: "Title",
        titlePlaceholder: "Title Placeholder",
        views: defaultViews,
      },
      slots: {
        text: () => "Text Content",
        details: () => "Details Content",
      },
    });

    expect(screen.getByRole("tablist")).toBeVisible();
    expect(screen.getByRole("tab", { name: /Text/ })).toBeVisible();
    expect(screen.getByRole("tab", { name: /Details/ })).toBeVisible();
  });

  it("selects first view by default when no view query param", async () => {
    useRouteMock.mockReturnValue({ query: {} });

    await renderSuspended(DocumentLayout, {
      props: {
        title: "Title",
        titlePlaceholder: "Title Placeholder",
        views: defaultViews,
      },
      slots: {
        text: () => "Text Content",
        details: () => "Details Content",
      },
    });

    expect(screen.getByRole("tab", { name: /Text/ })).toHaveAttribute(
      "aria-selected",
      "true",
    );
    expect(screen.getByRole("tab", { name: /Details/ })).toHaveAttribute(
      "aria-selected",
      "false",
    );
    expect(screen.getByText("Text Content")).toBeVisible();
  });

  it("selects view based on URL query param", async () => {
    useRouteMock.mockReturnValue({ query: { view: "details" } });

    await renderSuspended(DocumentLayout, {
      props: {
        title: "Title",
        titlePlaceholder: "Title Placeholder",
        views: defaultViews,
      },
      slots: {
        text: () => "Text Content",
        details: () => "Details Content",
      },
    });

    expect(screen.getByRole("tab", { name: /Text/ })).toHaveAttribute(
      "aria-selected",
      "false",
    );
    expect(screen.getByRole("tab", { name: /Details/ })).toHaveAttribute(
      "aria-selected",
      "true",
    );
    expect(screen.getByText("Details Content")).toBeVisible();
  });
});
