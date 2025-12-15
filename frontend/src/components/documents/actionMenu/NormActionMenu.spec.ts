import { shallowMount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ActionMenu from "~/components/documents/actionMenu/ActionMenu.vue";
import NormActionMenu from "~/components/documents/actionMenu/NormActionMenu.vue";
import type { LegislationWork } from "~/types";
import { getManifestationUrl } from "~/utils/norm";

vi.mock("~/utils/norm", () => ({
  getManifestationUrl: vi.fn(),
}));

describe("NormActionMenu", () => {
  const mockLegislationWork = {
    legislationIdentifier: "eli/bgbl-test/etc",
  } as LegislationWork;

  const workUrl = mockLegislationWork.legislationIdentifier;
  const expressionUrl = workUrl + "/expression";
  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal("location", {
      href: expressionUrl,
    });
  });

  it("passes correct props to ActionMenu", () => {
    const expectedXmlUrl = expressionUrl + "/manifestation/xml";
    const expectedTranslationUrl = "/translations/abc";
    vi.mocked(getManifestationUrl).mockReturnValue(expectedXmlUrl);

    const wrapper = shallowMount(NormActionMenu, {
      props: {
        metadata: mockLegislationWork,
        translationUrl: expectedTranslationUrl,
      },
    });

    const actionMenu = wrapper.findComponent(ActionMenu);
    expect(getManifestationUrl).toHaveBeenCalledExactlyOnceWith(
      mockLegislationWork,
      "application/xml",
    );
    expect(actionMenu.props()).toMatchObject({
      link: {
        url: "eli/bgbl-test/etc",
        label: "Link zur jeweils gültigen Fassung",
      },
      permalink: {
        url: "eli/bgbl-test/etc/expression",
        label: "Permalink zu dieser Fassung",
      },
      xmlUrl: expectedXmlUrl,
      translationUrl: expectedTranslationUrl,
    });
  });
});
