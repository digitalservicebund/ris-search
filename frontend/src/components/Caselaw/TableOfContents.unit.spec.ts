import { mountSuspended } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import TableOfContents from "./TableOfContents.vue";
import type { VueWrapper } from "@vue/test-utils";

describe("TableOfContents.vue", async () => {
  let wrapper: VueWrapper;

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

  beforeEach(async () => {
    vi.restoreAllMocks();
    wrapper = await mountSuspended(TableOfContents, {
      props: { tableOfContentEntries },
    });
  });

  it("renders page content links correctly", async () => {
    const links = wrapper.findAll("a");
    expect(links.length).toBe(tableOfContentEntries.length);
    tableOfContentEntries.forEach((item, index) => {
      expect(links.at(index)?.attributes("href")).toBe(`#${item.id}`);
      expect(links.at(index)?.text()).toContain(item.title);
    });
  });

  it("renders the correct icon for table of content", async () => {
    [
      "IcBaselineShortText",
      "IcBaselineSubject",
      "IcBaselineGavel",
      "IcOutlineFactCheck",
      "IcBaselineFormatListBulleted",
      "IcBaselineNotes",
    ].forEach((component) =>
      expect(wrapper.findComponent({ name: component }).exists()).toBe(true),
    );
  });

  it("does not call selectItem function when elements are not intersecting", async () => {
    const selectItemSpy = vi.spyOn(
      wrapper.vm as unknown as typeof TableOfContents,
      "selectEntry",
    );
    Object.defineProperty(window, "scrollY", { value: 0, writable: true });
    window.dispatchEvent(new Event("scroll"));

    await wrapper.vm.$nextTick();

    expect(selectItemSpy).not.toHaveBeenCalled();
  });

  it("updates selectedItem based on the route hash", async () => {
    const router = useRouter();
    await router.push("/#leitsatz"); // tests fail without this duplication when running the whole file
    wrapper = await mountSuspended(TableOfContents, {
      props: { tableOfContentEntries },
      route: { path: "/", hash: "#leitsatz" },
      shallow: true,
    });
    expect(router.currentRoute.value.hash).toBe("#leitsatz");
    expect(
      (wrapper.vm as unknown as typeof TableOfContents).selectedEntry.value,
    ).toBe("leitsatz");
  });

  it("replaces the route on hash change", async () => {
    const router = useRouter();
    const routerReplace = vi.spyOn(router, "replace");

    const wrapper = await mountSuspended(TableOfContents, {
      props: { tableOfContentEntries },
    });

    const link = wrapper.find(`a[href="#${tableOfContentEntries[1].id}"]`);
    await link.trigger("click");

    await wrapper.vm.$nextTick();
    expect(routerReplace).toHaveBeenCalledWith("/");
  });
});
