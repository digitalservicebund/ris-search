import { shallowMount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import ActionMenuWrapper from "~/components/documents/actionMenu/ActionMenuWrapper.vue";
import AdministrativeDirectiveActionMenu from "~/components/documents/actionMenu/AdministrativeDirectiveActionMenu.vue";
import type { AdministrativeDirective } from "~/types";

describe("AdministrativeDirectiveActionMenu", () => {
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

    const wrapper = shallowMount(AdministrativeDirectiveActionMenu, {
      props: {
        administrativeDirective: mockedAdministrativeDirective,
      },
    });

    const actionMenu = wrapper.findComponent(ActionMenuWrapper);
    expect(actionMenu.props()).toMatchObject({
      permalink: {
        label: "Link kopieren",
        url: "https://test.com",
      },
      xmlUrl: "/administrative-directive/XXLU000000001.xml",
    });
  });
});
