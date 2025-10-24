import { shallowMount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import NormActionsMenu from "~/components/ActionMenu/NormActionsMenu.vue";
import { useBackendURL } from "~/composables/useBackendURL";
import type { LegislationWork } from "~/types";
import { getManifestationUrl } from "~/utils/norm";

vi.mock("~/utils/norm", () => ({
  getManifestationUrl: vi.fn(),
}));

vi.mock("~/composables/useBackendURL", () => ({
  useBackendURL: vi.fn(),
}));

describe("NormActionsMenu.vue", () => {
  const normsBaseUrl = "https://legislation.example.com/";
  const mockLegislationWork = {
    legislationIdentifier: "eli/bgbl-test/etc",
  } as LegislationWork;

  const workUrl = normsBaseUrl + mockLegislationWork.legislationIdentifier;
  const expressionUrl = workUrl + "/expression";
  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal("location", {
      href: expressionUrl,
    });
  });

  it("passes correct props to ActionMenu", () => {
    vi.mocked(useBackendURL).mockReturnValue(normsBaseUrl);
    const expectedXmlUrl = expressionUrl + "/manifestation/xml";
    vi.mocked(getManifestationUrl).mockReturnValue(expectedXmlUrl);

    const wrapper = shallowMount(NormActionsMenu, {
      props: {
        metadata: mockLegislationWork,
        hasTranslation: undefined,
      },
    });

    const actionsMenu = wrapper.findComponent(ActionsMenu);
    expect(getManifestationUrl).toHaveBeenCalledExactlyOnceWith(
      mockLegislationWork,
      normsBaseUrl,
      "application/xml",
    );
    expect(actionsMenu.props()).toMatchObject({
      link: {
        url: "https://legislation.example.com/eli/bgbl-test/etc",
        label: "Link zur jeweils gültigen Fassung",
      },
      permalink: {
        url: "https://legislation.example.com/eli/bgbl-test/etc/expression",
        label: "Permalink zu dieser Fassung",
      },
      xmlUrl: expectedXmlUrl,
    });
  });
});
