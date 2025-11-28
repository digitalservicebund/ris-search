import { shallowMount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import LiteratureActionsMenu from "~/components/ActionMenu/LiteratureActionsMenu.vue";
import type { Literature } from "~/types";

describe("LiteratureActionsMenu", () => {
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

    const wrapper = shallowMount(LiteratureActionsMenu, {
      props: {
        literature: mockedLiterature,
      },
    });

    const actionsMenu = wrapper.findComponent(ActionsMenu);
    expect(actionsMenu.props()).toMatchObject({
      permalink: {
        label: "Link kopieren",
        url: "https://test.com",
      },
      xmlUrl: "/literature/XXLU000000001.xml",
    });
  });
});
