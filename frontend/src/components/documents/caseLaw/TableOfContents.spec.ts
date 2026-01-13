import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect, vi, beforeEach } from "vitest";
import TableOfContents from "./TableOfContents.vue";

describe("TableOfContents", async () => {
  const tableOfContentEntries = [
    {
      id: "leitsatz",
      title: "Leitsatz",
    },
    {
      id: "orientierungssatz",
      title: "Orientierungssatz",
    },
    {
      id: "tenor",
      title: "Tenor",
    },
    {
      id: "tatbestand",
      title: "Tatbestand",
    },
    {
      id: "entscheidungsgruende",
      title: "Entscheidungsgründe",
    },
    {
      id: "other",
      title: "Other",
    },
  ];

  async function renderTableOfContents() {
    return await renderSuspended(TableOfContents, {
      props: { tableOfContentEntries },
    });
  }

  beforeEach(async () => {
    vi.restoreAllMocks();
  });

  it("renders page content links correctly", async () => {
    await renderTableOfContents();

    const links = screen.getAllByRole("link");

    expect(links).toHaveLength(tableOfContentEntries.length);

    tableOfContentEntries.forEach((item, index) => {
      expect(links[index]).toHaveAttribute(
        "href",
        expect.stringContaining(`/#${item.id}`),
      );

      expect(links[index]).toHaveTextContent(item.title);
    });
  });

  it("does not update the selected entry when items are not intersecting", async () => {
    await renderTableOfContents();

    vi.spyOn(globalThis, "scrollY", "get").mockReturnValue(0);
    globalThis.dispatchEvent(new Event("scroll"));

    await nextTick();

    const links = screen.getAllByRole("link");

    links.forEach((link) => expect(link).not.toHaveAttribute("aria-current"));
  });

  it("updates selected item based on the route hash", async () => {
    // Create the target element that the router will try to scroll to
    const targetElement = document.createElement("div");
    targetElement.id = "leitsatz";
    document.body.appendChild(targetElement);

    await renderSuspended(TableOfContents, {
      props: { tableOfContentEntries },
      route: { path: "/", hash: "#leitsatz" },
    });

    expect(screen.getByRole("link", { name: "Leitsatz" })).toHaveAttribute(
      "aria-current",
      "location",
    );

    // Clean up
    targetElement.remove();
  });

  it("navigates on click", async () => {
    const user = userEvent.setup();

    const router = useRouter();
    const routerReplace = vi.spyOn(router, "replace");

    await renderTableOfContents();

    const link = screen.getByRole("link", { name: "Orientierungssatz" });
    await user.click(link);

    expect(routerReplace).toHaveBeenCalledWith("/");
  });
});
