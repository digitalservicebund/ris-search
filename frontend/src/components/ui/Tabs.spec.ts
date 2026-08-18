import userEvent from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import Tab from "./Tab.vue";
import Tabs from "./Tabs.vue";

const renderTabs = () =>
  render(Tabs, {
    attrs: { "aria-label": "Ansichten" },
    slots: {
      default: [
        `<Tab active>Text</Tab>`,
        `<Tab>Details</Tab>`,
        `<Tab>Fassungen</Tab>`,
      ].join(""),
    },
    global: { components: { Tab } },
  });

const tab = (name: string) => screen.getByRole("tab", { name });

describe("Tabs", () => {
  it("renders a horizontal tab list containing the tabs", () => {
    renderTabs();

    const tabList = screen.getByRole("tablist");

    expect(tabList).toHaveAttribute("aria-orientation", "horizontal");
    expect(screen.getAllByRole("tab")).toHaveLength(3);
  });

  it("labels the tab list with attributes passed to it", () => {
    renderTabs();

    expect(screen.getByRole("tablist", { name: "Ansichten" })).toBeVisible();
  });

  it("moves focus to the next tab on ArrowRight, wrapping around", async () => {
    renderTabs();

    tab("Text").focus();

    await userEvent.keyboard("{ArrowRight}");
    expect(tab("Details")).toHaveFocus();

    await userEvent.keyboard("{ArrowRight}");
    expect(tab("Fassungen")).toHaveFocus();

    await userEvent.keyboard("{ArrowRight}");
    expect(tab("Text")).toHaveFocus();
  });

  it("moves focus to the previous tab on ArrowLeft, wrapping around", async () => {
    renderTabs();

    tab("Text").focus();

    await userEvent.keyboard("{ArrowLeft}");
    expect(tab("Fassungen")).toHaveFocus();

    await userEvent.keyboard("{ArrowLeft}");
    expect(tab("Details")).toHaveFocus();
  });

  it("moves focus to the first and last tab on Home and End", async () => {
    renderTabs();

    tab("Details").focus();

    await userEvent.keyboard("{End}");
    expect(tab("Fassungen")).toHaveFocus();

    await userEvent.keyboard("{Home}");
    expect(tab("Text")).toHaveFocus();
  });

  it("does not select the tab that receives focus", async () => {
    renderTabs();

    tab("Text").focus();

    await userEvent.keyboard("{ArrowRight}");

    expect(tab("Details")).toHaveFocus();
    expect(tab("Details")).toHaveAttribute("aria-selected", "false");
    expect(tab("Text")).toHaveAttribute("aria-selected", "true");
  });

  it("ignores arrow keys pressed outside of a tab", async () => {
    render(Tabs, {
      slots: { default: `<button type="button">Not a tab</button>` },
    });

    const button = screen.getByRole("button", { name: "Not a tab" });
    button.focus();

    await userEvent.keyboard("{ArrowRight}");

    expect(button).toHaveFocus();
  });
});
