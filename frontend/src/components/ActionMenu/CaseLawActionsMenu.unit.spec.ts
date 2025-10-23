import { shallowMount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import CaseLawActionsMenu from "~/components/ActionMenu/CaseLawActionsMenu.vue";
import { useBackendURL } from "~/composables/useBackendURL";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLaw";

vi.mock("~/utils/caseLaw", () => ({
  getEncodingURL: vi.fn(),
}));

vi.mock("~/composables/useBackendURL", () => ({
  useBackendURL: vi.fn(),
}));

describe("CaseLawActionsMenu.vue", () => {
  it("passes correct props to ActionMenu", () => {
    const baseUrl = "https://legislation.example.com/";
    vi.mocked(useBackendURL).mockReturnValue(baseUrl);

    const expectedXmlUrl = baseUrl + "/foo/foo.xml";
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
      baseUrl,
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
