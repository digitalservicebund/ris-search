import { shallowMount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import AdministrativeDirectiveActionsMenu from "~/components/ActionMenu/AdministrativeDirectiveActionsMenu.vue";
import type { AdministrativeDirective } from "~/types";

describe("AdministrativeDirectiveActionsMenu", () => {
  it("passes correct props to ActionMenu", () => {
    vi.stubGlobal("location", {
      href: "https://test.com",
    });

    const mockedAdministrativeDirective = {
      encoding: [
        {
          contentUrl: "/administrative-directive/XXLU000000001.xml",
          encodingFormat: "application/xml",
        },
      ],
    } as AdministrativeDirective;

    const wrapper = shallowMount(AdministrativeDirectiveActionsMenu, {
      props: {
        administrativeDirective: mockedAdministrativeDirective,
      },
    });

    const actionsMenu = wrapper.findComponent(ActionsMenu);
    expect(actionsMenu.props()).toMatchObject({
      permalink: {
        label: "Link kopieren",
        url: "https://test.com",
      },
      xmlUrl: "/administrative-directive/XXLU000000001.xml",
    });
  });
});
