import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, within } from "@testing-library/vue";
import RisBreadcrumb from "./Breadcrumbs.vue";

const nuxtLinkStub = {
  template: "<a :href=\"typeof to === 'string' ? to : '#'\"><slot /></a>",
  props: ["to"],
};

describe("Breadcrumbs", () => {
  it("renders Start and all items except the last as links", async () => {
    await renderSuspended(RisBreadcrumb, {
      props: {
        items: [
          { label: "Foo", route: "/foo" },
          { label: "LastItem", route: "/bar" },
        ],
      },
      global: { stubs: { NuxtLink: nuxtLinkStub } },
    });

    expect(screen.getByRole("link", { name: "Start" })).toBeVisible();

    const fooLink = screen.getByRole("link", { name: "Foo" });
    expect(fooLink).toBeVisible();
    expect(fooLink).toHaveAttribute("href", "/foo");

    expect(screen.getByText("LastItem")).toBeVisible();
    expect(
      screen.queryByRole("link", { name: "LastItem" }),
    ).not.toBeInTheDocument();
  });

  it("uses a custom aria-label on the navigation landmark", async () => {
    await renderSuspended(RisBreadcrumb, {
      props: { label: "Navigation" },
    });

    expect(
      screen.getByRole("navigation", { name: "Navigation" }),
    ).toBeVisible();
  });

  it("uses 'Pfadnavigation' as the default aria-label on the navigation landmark", async () => {
    await renderSuspended(RisBreadcrumb);

    expect(
      screen.getByRole("navigation", { name: "Pfadnavigation" }),
    ).toBeVisible();
  });

  describe("collapsing", () => {
    it("does not collapse when the collapse prop is false (default)", async () => {
      await renderSuspended(RisBreadcrumb, {
        props: {
          items: [
            { label: "A", route: "/a" },
            { label: "B", route: "/b" },
            { label: "C", route: "/c" },
            { label: "Last" },
          ],
        },
        global: { stubs: { NuxtLink: nuxtLinkStub } },
      });

      expect(
        screen.queryByRole("button", { name: "Navigiere zu" }),
      ).not.toBeInTheDocument();
      expect(screen.getByRole("link", { name: "A" })).toBeVisible();
      expect(screen.getByRole("link", { name: "B" })).toBeVisible();
      expect(screen.getByRole("link", { name: "C" })).toBeVisible();
    });

    it("does not collapse when enabled and total items ≤ 3 (including home)", async () => {
      await renderSuspended(RisBreadcrumb, {
        props: {
          collapse: true,
          items: [{ label: "A", route: "/a" }, { label: "Last" }],
        },
        global: { stubs: { NuxtLink: nuxtLinkStub } },
      });

      expect(
        screen.queryByRole("button", { name: "Navigiere zu" }),
      ).not.toBeInTheDocument();
      expect(screen.getByRole("link", { name: "A" })).toBeVisible();
    });

    it("shows a collapse button when enabled and total items > 3 (including home)", async () => {
      await renderSuspended(RisBreadcrumb, {
        props: {
          collapse: true,
          items: [
            { label: "A", route: "/a" },
            { label: "B", route: "/b" },
            { label: "Last" },
          ],
        },
        global: { stubs: { NuxtLink: nuxtLinkStub } },
      });

      expect(
        screen.getByRole("button", { name: "Navigiere zu" }),
      ).toBeVisible();
      expect(screen.getByRole("link", { name: "Start" })).toBeVisible();
      expect(screen.getByText("Last")).toBeVisible();
      expect(screen.queryByRole("link", { name: "A" })).not.toBeInTheDocument();
      expect(screen.queryByRole("link", { name: "B" })).not.toBeInTheDocument();
    });

    it("opens a drawer with all items when the button is clicked", async () => {
      const user = userEvent.setup();
      await renderSuspended(RisBreadcrumb, {
        props: {
          collapse: true,
          items: [
            { label: "A", route: "/a" },
            { label: "B", route: "/b" },
            { label: "Last" },
          ],
        },
        global: { stubs: { NuxtLink: nuxtLinkStub } },
      });

      await user.click(screen.getByRole("button", { name: "Navigiere zu" }));

      const dialog = screen.getByRole("dialog");
      expect(dialog).toBeVisible();
      expect(within(dialog).getByRole("link", { name: "Start" })).toBeVisible();
      expect(within(dialog).getByRole("link", { name: "A" })).toHaveAttribute(
        "href",
        "/a",
      );
      expect(within(dialog).getByRole("link", { name: "B" })).toHaveAttribute(
        "href",
        "/b",
      );
      expect(within(dialog).getByText("Last")).toBeVisible();
      expect(
        within(dialog).queryByRole("link", { name: "Last" }),
      ).not.toBeInTheDocument();
    });

    it("shows extended label instead of label for links in the drawer", async () => {
      const user = userEvent.setup();
      await renderSuspended(RisBreadcrumb, {
        props: {
          collapse: true,
          items: [
            { label: "A", extendedLabel: "A extended", route: "/a" },
            { label: "B", route: "/b" },
            { label: "Last", extendedLabel: "Last extended" },
          ],
        },
        global: { stubs: { NuxtLink: nuxtLinkStub } },
      });

      await user.click(screen.getByRole("button", { name: "Navigiere zu" }));

      const dialog = screen.getByRole("dialog");
      expect(
        within(dialog).getByRole("link", { name: "A extended" }),
      ).toBeVisible();
      expect(
        within(dialog).queryByRole("link", { name: "A" }),
      ).not.toBeInTheDocument();
    });
  });
});
