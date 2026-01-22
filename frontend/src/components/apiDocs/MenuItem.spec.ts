import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { vi } from "vitest";
import MenuItem from "./MenuItem.vue";
import type { MenuItemProps } from "~/components/apiDocs/MenuItem.vue";

const NuxtLinkStub = {
  name: "NuxtLink",
  props: ["to"],
  template: `<a :href="to" v-bind="$attrs"><slot /></a>`,
};

const MdiChevronUpStub = {
  name: "MdiChevronUp",
  template: '<i data-testid="chev-up" />',
};
const MdiChevronRightStub = {
  name: "MdiChevronRight",
  template: '<i data-testid="chev-right" />',
};

const MenuItemChildStub = {
  name: "MenuItem",
  props: ["item", "root"],
  template: `<div data-testid="child" />`,
};

const { useRouteMock } = vi.hoisted(() => {
  return {
    useRouteMock: vi.fn(() => {
      return { path: "#", hash: "" };
    }),
  };
});

mockNuxtImport("useRoute", () => {
  return useRouteMock;
});

async function mountWithRoute({
  path = "/guide",
  hash = "",
  props,
}: {
  path?: string;
  hash?: string;
  props: MenuItemProps;
}) {
  useRouteMock.mockImplementation(() => {
    return { path, hash };
  });

  return await renderSuspended(MenuItem, {
    props,
    global: {
      stubs: {
        NuxtLink: NuxtLinkStub,
        MdiChevronUp: MdiChevronUpStub,
        MdiChevronRight: MdiChevronRightStub,
        MenuItem: MenuItemChildStub,
      },
    },
  });
}

describe("MenuItem", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("is active when route path matches and hash equals the item hash", async () => {
    const item = {
      text: "Getting started",
      link: "/guide#getting-started",
      items: [
        { text: "Overview", link: "/guide#overview" },
        { text: "Details", link: "/guide#details" },
      ],
    };

    await mountWithRoute({
      path: "/guide",
      hash: "#getting-started",
      props: { item, root: true },
    });

    const link = screen.getByRole("link", { name: "Getting started" });
    expect(link).toHaveAttribute("href", "#");
    expect(link).toHaveClass("border-l-4");
    expect(link).toHaveClass("text-blue-800");

    expect(screen.getByTestId("chev-up")).toBeInTheDocument();
    expect(screen.queryByTestId("chev-right")).not.toBeInTheDocument();

    expect(screen.getAllByTestId("child")).toHaveLength(2);
  });

  it("is inactive when path matches but hash does not match parent or any child", async () => {
    const item = {
      text: "Getting started",
      link: "/guide#getting-started",
      items: [
        { text: "Overview", link: "/guide#overview" },
        { text: "Details", link: "/guide#details" },
      ],
    };

    await mountWithRoute({
      path: "/guide/anything",
      hash: "#other-hash",
      props: { item, root: false },
    });

    const link = screen.getByRole("link", { name: "Getting started" });
    expect(link).not.toHaveClass("border-l-4");
    expect(screen.getByTestId("chev-right")).toBeInTheDocument();
    expect(screen.queryByTestId("chev-up")).not.toBeInTheDocument();
    expect(screen.queryAllByTestId("child")).toHaveLength(0);
  });

  it("becomes active via child hash match (matchingHashes includes children)", async () => {
    const item = {
      text: "Parent",
      link: "/guide#overview",
      items: [
        { text: "Deep", link: "/guide#deep-dive" },
        { text: "Tips", link: "/guide#pro-tips" },
      ],
    };

    await mountWithRoute({
      path: "/guide/advanced",
      hash: "#deep-dive",
      props: { item },
    });

    const link = screen.getByRole("link", { name: "Parent" });
    expect(link).toHaveClass("border-l-4");
    expect(screen.getByTestId("chev-up")).toBeInTheDocument();
    expect(screen.getAllByTestId("child")).toHaveLength(2);
  });

  it("is active when item has no hash and path matches", async () => {
    const item = {
      text: "Guide root",
      link: "/guide",
      items: [{ text: "Child", link: "/guide#child" }],
    };

    await mountWithRoute({
      path: "/guide/anything",
      hash: "#whatever",
      props: { item, root: true },
    });

    const link = screen.getByRole("link", { name: "Guide root" });
    expect(link).toHaveClass("border-l-4");
    expect(screen.getByTestId("chev-up")).toBeInTheDocument();
    expect(screen.getAllByTestId("child")).toHaveLength(1);
  });

  it("is inactive when route path does not start with item path", async () => {
    const item = {
      text: "Other section",
      link: "/docs#getting-started",
      items: [{ text: "Child", link: "/docs#child" }],
    };

    await mountWithRoute({
      path: "/guide/intro",
      hash: "#getting-started",
      props: { item },
    });

    const link = screen.getByRole("link", { name: "Other section" });
    expect(link).not.toHaveClass("border-l-4");
    expect(screen.getByTestId("chev-right")).toBeInTheDocument();
    expect(screen.queryAllByTestId("child")).toHaveLength(0);
  });

  it("does not emit click while menu is disabled (current implementation)", async () => {
    const item = {
      text: "Disabled link",
      link: "/guide#hash",
      items: [],
    };

    const user = userEvent.setup();
    const { emitted } = await mountWithRoute({
      path: "/guide",
      hash: "#hash",
      props: { item },
    });

    const link = screen.getByRole("link", { name: "Disabled link" });
    await user.click(link);

    expect(emitted("click")).toBeUndefined();
    expect(link).toHaveAttribute("href", "#");
  });
});
