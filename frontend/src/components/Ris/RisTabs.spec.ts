import { render, screen } from "@testing-library/vue";
import { nextTick } from "vue";
import RisTabs from "./RisTabs.vue";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

describe("RisTabs.vue", () => {
  const mockTabs = [
    {
      id: "text",
      href: "#text",
      label: "Text",
      ariaLabel: "Text der Übersetzung",
      icon: IcBaselineSubject,
      iconClass: "mr-8",
    },
    {
      id: "details",
      href: "#details",
      label: "Details",
      ariaLabel: "Details zur Übersetzung",
      icon: IcOutlineInfo,
      iconClass: "mr-8",
    },
  ];

  it("renders all tabs with correct labels, hrefs, and aria-labels", () => {
    render(RisTabs, {
      props: {
        tabs: mockTabs,
        ariaLabel: "Test tabs",
      },
    });

    const links = screen.getAllByRole("link");
    expect(links).toHaveLength(2);

    expect(links[0]).toHaveTextContent("Text");
    expect(links[0]).toHaveAttribute("href", "#text");
    expect(links[0]).toHaveAttribute("aria-label", "Text der Übersetzung");

    expect(links[1]).toHaveTextContent("Details");
    expect(links[1]).toHaveAttribute("href", "#details");
    expect(links[1]).toHaveAttribute("aria-label", "Details zur Übersetzung");
  });

  it("renders nav with correct aria-label", () => {
    render(RisTabs, {
      props: {
        tabs: mockTabs,
        ariaLabel: "Test tabs",
      },
    });

    const nav = screen.getByRole("navigation", { name: "Test tabs" });
    expect(nav).toBeVisible();
  });

  it("sets first tab as active by default", () => {
    render(RisTabs, {
      props: {
        tabs: mockTabs,
        ariaLabel: "Test tabs",
      },
    });

    const links = screen.getAllByRole("link");
    expect(links[0]).toHaveAttribute("aria-current", "page");
    expect(links[1]).not.toHaveAttribute("aria-current");
  });

  it("uses defaultTab prop when provided", () => {
    render(RisTabs, {
      props: {
        tabs: mockTabs,
        ariaLabel: "Test tabs",
        defaultTab: "details",
      },
    });

    const links = screen.getAllByRole("link");
    expect(links[0]).not.toHaveAttribute("aria-current");
    expect(links[1]).toHaveAttribute("aria-current", "page");
  });

  it("switches active tab on click when JS is enabled", async () => {
    render(RisTabs, {
      props: {
        tabs: mockTabs,
        ariaLabel: "Test tabs",
      },
    });

    await nextTick();

    const links = screen.getAllByRole("link");
    expect(links[0]).toHaveAttribute("aria-current", "page");
    expect(links[1]).not.toHaveAttribute("aria-current");

    await links[1]?.click();

    const updatedLinks = screen.getAllByRole("link");
    expect(updatedLinks[0]).not.toHaveAttribute("aria-current");
    expect(updatedLinks[1]!).toHaveAttribute("aria-current", "page");
  });

  it("renders tabs without icons when icon is not provided", () => {
    const tabsWithoutIcons = [
      {
        id: "tab1",
        href: "#tab1",
        label: "Tab 1",
        ariaLabel: "Tab 1",
      },
      {
        id: "tab2",
        href: "#tab2",
        label: "Tab 2",
        ariaLabel: "Tab 2",
      },
    ];

    render(RisTabs, {
      props: {
        tabs: tabsWithoutIcons,
        ariaLabel: "Test tabs",
      },
    });

    const links = screen.getAllByRole("link");
    expect(links).toHaveLength(2);
    expect(links[0]).toHaveTextContent("Tab 1");
    expect(links[1]).toHaveTextContent("Tab 2");
  });
});
