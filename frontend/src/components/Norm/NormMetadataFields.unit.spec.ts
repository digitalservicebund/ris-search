import { describe, it, expect, vi } from "vitest";
import NormMetadataFields from "~/components/Norm/NormMetadataFields.vue";
import MetadataField from "~/components/MetadataField.vue";
import { mount } from "@vue/test-utils";
import * as Config from "~/utils/config";

describe("NormMetadataFields.vue", () => {
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

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Status",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe(expectedStatus);
  });

  it("shows status historical", () => {
    const expectedStatus = "Außer Kraft";
    const wrapper = mount(NormMetadataFields, {
      props: {
        status: "Expired",
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Status",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe(expectedStatus);
  });

  it("shows status future", () => {
    const expectedStatus = "Zukünftig in Kraft";
    const wrapper = mount(NormMetadataFields, {
      props: {
        status: "FutureInForce",
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Status",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe(expectedStatus);
  });

  it("shows valid from date", () => {
    const expectedValidFrom = "01.01.2025";
    const wrapper = mount(NormMetadataFields, {
      props: {
        validFrom: parseDateGermanLocalTime("2025-01-01"),
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Gültig ab",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe(expectedValidFrom);
  });

  it("shows placeholder if valid from date is undefined", () => {
    const wrapper = mount(NormMetadataFields);

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Gültig ab",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe("-");
  });

  it("shows valid to date", () => {
    const expectedValidTo = "01.12.2025";
    const wrapper = mount(NormMetadataFields, {
      props: {
        validTo: parseDateGermanLocalTime("2025-12-01"),
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Gültig bis",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe(expectedValidTo);
  });

  it("shows placeholder if valid to date is undefined", () => {
    const wrapper = mount(NormMetadataFields);

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Gültig bis",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe("-");
  });

  it("hides valid from and to dates on prototype", () => {
    const mockedIsPrototypeProfile = vi.spyOn(Config, "isPrototypeProfile");
    mockedIsPrototypeProfile.mockReturnValue(true);

    const wrapper = mount(NormMetadataFields, {});

    expect(wrapper.find("#validFrom").exists()).toBeFalsy();
    expect(wrapper.find("#validTo").exists()).toBeFalsy();
  });
});
