import { mount } from "@vue/test-utils";
import { describe, it, expect, vi, afterEach } from "vitest";
import MetadataField from "~/components/MetadataField.vue";
import NormMetadataFields from "~/components/Norm/Metadatafields/NormMetadataFields.vue";
import ValidityDatesMetadataFields from "~/components/Norm/Metadatafields/ValidityDatesMetadataFields.vue";
import * as Config from "~/utils/config";

describe("NormMetadataFields.vue", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("shows abbreviation", () => {
    const expectedAbbreviation = "FooBar";
    const wrapper = mount(NormMetadataFields, {
      props: {
        abbreviation: expectedAbbreviation,
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Abkürzung",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe(expectedAbbreviation);
  });

  it("does not show abbreviation if abbreviation is undefined", () => {
    const wrapper = mount(NormMetadataFields);
    expect(wrapper.find("#abbreviation").exists()).toBeFalsy();
  });

  it("shows status inForce", () => {
    const expectedStatus = "Aktuell gültig";
    const wrapper = mount(NormMetadataFields, {
      props: {
        status: "InForce",
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const statusFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Status",
    );

    expect(statusFields).toHaveLength(1);
    expect(statusFields[0].props().value).toBe(expectedStatus);
  });

  it("shows status historical", () => {
    const expectedStatus = "Außer Kraft";
    const wrapper = mount(NormMetadataFields, {
      props: {
        status: "Expired",
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const statusFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Status",
    );

    expect(statusFields).toHaveLength(1);
    expect(statusFields[0].props().value).toBe(expectedStatus);
  });

  it("shows status future", () => {
    const expectedStatus = "Zukünftig in Kraft";
    const wrapper = mount(NormMetadataFields, {
      props: {
        status: "FutureInForce",
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const statusFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Status",
    );

    expect(statusFields).toHaveLength(1);
    expect(statusFields[0].props().value).toBe(expectedStatus);
  });

  it("shows validity dates metadata fields", () => {
    const validFrom = parseDateGermanLocalTime("2025-01-01");
    const validTo = parseDateGermanLocalTime("2025-06-01");

    const wrapper = mount(NormMetadataFields, {
      props: {
        validFrom: validFrom,
        validTo: validTo,
      },
    });

    const validityDatesMetadataFields = wrapper.findComponent(
      ValidityDatesMetadataFields,
    );

    expect(validityDatesMetadataFields.exists()).toBeTruthy();
    expect(validityDatesMetadataFields.props().validFrom).toStrictEqual(
      validFrom,
    );
    expect(validityDatesMetadataFields.props().validTo).toStrictEqual(validTo);
  });

  it("hides valid from and to fields on prototype", () => {
    const mockedIsPrototypeProfile = vi.spyOn(Config, "isPrototypeProfile");
    mockedIsPrototypeProfile.mockReturnValue(true);

    const wrapper = mount(NormMetadataFields, {});

    expect(
      wrapper.findComponent(ValidityDatesMetadataFields).exists(),
    ).toBeFalsy();
  });
});
