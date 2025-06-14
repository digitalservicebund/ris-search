import { mount } from "@vue/test-utils";
import VersionWarningMessage from "./VersionWarningMessage.vue";
import type { LegislationWork, SearchResult } from "~/types";

describe("VersionWarningMessage", () => {
  const testVersions = [
    {
      item: {
        legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/regelungstext-1",
        workExample: {
          temporalCoverage: "2020-01-01/2022-12-31",
          legislationIdentifier:
            "eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu/regelungstext-1",
          legislationLegalForce: "NotInForce",
        },
      },
    },
    {
      item: {
        legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/regelungstext-1",
        workExample: {
          temporalCoverage: "2023-01-01/2923-12-31",
          legislationIdentifier:
            "eli/bund/bgbl-1/2020/s1126/2023-01-01/1/deu/regelungstext-1",
          legislationLegalForce: "InForce",
        },
      },
    },
    {
      item: {
        legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/regelungstext-1",
        workExample: {
          temporalCoverage: "2924-01-01/..",
          legislationIdentifier:
            "eli/bund/bgbl-1/2020/s1126/2924-01-01/1/deu/regelungstext-1",
          legislationLegalForce: "NotInForce",
        },
      },
    },
  ] as SearchResult<LegislationWork>[];

  it("renders message when a future version exists for the current in force version", () => {
    const wrapper = mount(VersionWarningMessage, {
      props: {
        versions: testVersions,
        currentExpression:
          testVersions[1].item.workExample.legislationIdentifier,
      },
      global: { stubs: ["RouterLink"] },
    });
    expect(wrapper.html()).toContain("Neue Fassung ab 01.01.2924");
    expect(wrapper.html()).toContain(
      `/norms/${testVersions[2].item.workExample.legislationIdentifier}`,
    );
  });

  it("renders message when the current version is a future version", () => {
    const wrapper = mount(VersionWarningMessage, {
      props: {
        versions: testVersions,
        currentExpression: `${testVersions[0].item.workExample.legislationIdentifier}`,
      },
      global: { stubs: ["RouterLink"] },
    });
    expect(wrapper.html()).toContain("Historische Fassung.");
    expect(wrapper.html()).toContain(
      `/norms/${testVersions[1].item.workExample.legislationIdentifier}`,
    );
  });

  it("renders message when the current version is a historical version", () => {
    const wrapper = mount(VersionWarningMessage, {
      props: {
        versions: testVersions,
        currentExpression: `${testVersions[2].item.workExample.legislationIdentifier}`,
      },
      global: { stubs: ["RouterLink"] },
    });
    expect(wrapper.html()).toContain("Zukünftige Fassung.");
    expect(wrapper.html()).toContain(
      `/norms/${testVersions[1].item.workExample.legislationIdentifier}`,
    );
  });

  it("does not render a message if there are no future versions existing for the current in force version", () => {
    const wrapper = mount(VersionWarningMessage, {
      props: {
        versions: [testVersions[0], testVersions[1]],
        currentExpression: `${testVersions[1].item.workExample.legislationIdentifier}`,
      },
      global: { stubs: ["RouterLink"] },
    });
    expect(wrapper.html()).not.toContain("Neue Fassung");
    expect(wrapper.findComponent({ name: "Message" }).exists()).toBe(false);
  });
});
