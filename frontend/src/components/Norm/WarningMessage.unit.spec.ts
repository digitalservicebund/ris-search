import { mount } from "@vue/test-utils";
import WarningMessage from "./WarningMessage.vue";
import type { LegislationWork, SearchResult } from "~/types";
import type { WarningMessageProps } from "~/components/Norm/WarningMessage.vue";

const baseProps = {
  inForceVersionLink: "/norms/eli/bund/bgbl-1/2000/s100/2000-01-01/1/deu",
  historicalWarningMessage: "Paragraf einer historischen Fassung.",
  futureWarningMessage: "Paragraph einer zukünftigen Fassung.",
};

const futureVersion = {
  item: {
    workExample: {
      legislationIdentifier: "future-id",
      temporalCoverage: "2100-01-01/..",
    },
  },
  versionDates: ["2100-01-01"],
} as unknown as SearchResult<LegislationWork>;

function getWrapper(customProps: WarningMessageProps) {
  return mount(WarningMessage, {
    props: customProps,
    global: {
      stubs: {
        NuxtLink: { template: "<a><slot></slot></a>" },
        IcBaselineHistory: true,
      },
    },
  });
}

describe("WarningMessage.vue", () => {
  it("shows info message for inForce with futureVersion", () => {
    const wrapper = getWrapper({
      ...baseProps,
      currentVersionStatus: "inForce",
      futureVersion: futureVersion.item,
    });
    expect(wrapper.find("[data-testid='norm-warning-message']").exists()).toBe(
      true,
    );
    expect(wrapper.text()).toContain("Neue Fassung ab 01.01.2100");
    expect(wrapper.text()).toContain("Zur zukünftigen Fassung");
  });

  it("shows no message for inForce without futureVersion", () => {
    const wrapper = getWrapper({
      ...baseProps,
      currentVersionStatus: "inForce",
    });
    expect(wrapper.find("[data-testid='norm-warning-message']").exists()).toBe(
      false,
    );
  });

  it("shows warning for historical version", () => {
    const wrapper = getWrapper({
      ...baseProps,
      currentVersionStatus: "historical",
    });
    expect(wrapper.find("[data-testid='norm-warning-message']").exists()).toBe(
      true,
    );
    expect(wrapper.text()).toContain("Paragraf einer historischen Fassung.");
    expect(wrapper.text()).toContain("Zur aktuell gültigen Fassung");
  });

  it("shows warning for future version", () => {
    const wrapper = getWrapper({
      ...baseProps,
      currentVersionStatus: "future",
    });
    expect(wrapper.find("[data-testid='norm-warning-message']").exists()).toBe(
      true,
    );
    expect(wrapper.text()).toContain("Paragraph einer zukünftigen Fassung.");
    expect(wrapper.text()).toContain("Zur aktuell gültigen Fassung");
  });
});
