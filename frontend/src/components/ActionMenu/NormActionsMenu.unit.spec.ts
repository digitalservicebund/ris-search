import { shallowMount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import NormActionsMenu from "~/components/ActionMenu/NormActionsMenu.vue";
import type { LegislationWork } from "~/types";
import { getManifestationUrl } from "~/utils/norm";

vi.mock("~/utils/norm", () => ({
  getManifestationUrl: vi.fn(),
}));

describe("NormActionsMenu.vue", () => {
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

    const wrapper = shallowMount(NormActionsMenu, {
      props: {
        metadata: mockLegislationWork,
        translationUrl: expectedTranslationUrl,
      },
    });

    const actionsMenu = wrapper.findComponent(ActionsMenu);
    expect(getManifestationUrl).toHaveBeenCalledExactlyOnceWith(
      mockLegislationWork,
      "application/xml",
    );
    expect(actionsMenu.props()).toMatchObject({
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
