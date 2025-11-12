import { shallowMount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import CaseLawActionsMenu from "~/components/ActionMenu/CaseLawActionsMenu.vue";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLaw";

vi.mock("~/utils/caseLaw", () => ({
  getEncodingURL: vi.fn(),
}));

describe("CaseLawActionsMenu.vue", () => {
  it("passes correct props to ActionMenu", () => {
    const expectedXmlUrl = "/foo/foo.xml";
    vi.mocked(getEncodingURL).mockReturnValue(expectedXmlUrl);

    const mockedCaseLaw = {} as CaseLaw;

    const wrapper = shallowMount(CaseLawActionsMenu, {
      props: {
        caseLaw: mockedCaseLaw,
      },
    });

    const actionsMenu = wrapper.findComponent(ActionsMenu);
    expect(getEncodingURL).toHaveBeenCalledExactlyOnceWith(
      mockedCaseLaw,
      "application/xml",
    );
    expect(actionsMenu.props()).toMatchObject({
      permalink: {
        label: "Link kopieren",
        disabled: true,
      },
      xmlUrl: expectedXmlUrl,
    });
  });
});
