import { shallowMount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import ActionMenuWrapper from "~/components/documents/actionMenu/ActionMenuWrapper.vue";
import LiteratureActionMenu from "~/components/documents/actionMenu/LiteratureActionMenu.vue";
import type { Literature } from "~/types";

describe("LiteratureActionMenu", () => {
  it("passes correct props to ActionMenu", () => {
    vi.stubGlobal("location", {
      href: "https://test.com",
    });

    const mockedLiterature = {
      encoding: [
        {
          contentUrl: "/literature/XXLU000000001.xml",
          encodingFormat: "application/xml",
        },
      ],
    } as Literature;

    const wrapper = shallowMount(LiteratureActionMenu, {
      props: {
        literature: mockedLiterature,
      },
    });

    const actionMenu = wrapper.findComponent(ActionMenuWrapper);
    expect(actionMenu.props()).toMatchObject({
      permalink: {
        label: "Link kopieren",
        url: "https://test.com",
      },
      xmlUrl: "/literature/XXLU000000001.xml",
    });
  });
});
