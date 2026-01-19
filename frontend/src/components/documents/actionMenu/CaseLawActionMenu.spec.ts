import { shallowMount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import ActionMenuWrapper from "~/components/documents/actionMenu/ActionMenuWrapper.vue";
import CaseLawActionMenu from "~/components/documents/actionMenu/CaseLawActionMenu.vue";
import type { CaseLaw } from "~/types";
import { getEncodingURL } from "~/utils/caseLaw";

vi.mock("~/utils/caseLaw", () => ({
  getEncodingURL: vi.fn(),
}));

describe("CaseLawActionMenu", () => {
  it("passes correct props to ActionMenu", () => {
    const expectedXmlUrl = "/foo/foo.xml";
    vi.mocked(getEncodingURL).mockReturnValue(expectedXmlUrl);

    const mockedCaseLaw = {} as CaseLaw;

    const wrapper = shallowMount(CaseLawActionMenu, {
      props: {
        caseLaw: mockedCaseLaw,
      },
    });

    const actionMenu = wrapper.findComponent(ActionMenuWrapper);
    expect(getEncodingURL).toHaveBeenCalledExactlyOnceWith(
      mockedCaseLaw,
      "application/xml",
    );
    expect(actionMenu.props()).toMatchObject({
      permalink: {
        label: "Link kopieren",
        disabled: true,
      },
      xmlUrl: expectedXmlUrl,
    });
  });
});
