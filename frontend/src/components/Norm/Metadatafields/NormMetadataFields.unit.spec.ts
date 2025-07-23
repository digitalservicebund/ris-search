import { mount } from "@vue/test-utils";
import { describe, it, expect, vi, afterEach } from "vitest";
import MetadataField from "~/components/MetadataField.vue";
import NormMetadataFields from "~/components/Norm/Metadatafields/NormMetadataFields.vue";
import ValidFromField from "~/components/Norm/Metadatafields/ValidFromField.vue";
import ValidToField from "~/components/Norm/Metadatafields/ValidToField.vue";
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

  it("shows valid-from metadata field", () => {
    const inputDate = parseDateGermanLocalTime("2025-01-01");
    const wrapper = mount(NormMetadataFields, {
      props: {
        validFrom: inputDate,
      },
    });

    const validFromField = wrapper.findComponent(ValidFromField);
    expect(validFromField.props().value).toStrictEqual(inputDate);
  });

  it("shows valid-to metadata field", () => {
    const inputDate = parseDateGermanLocalTime("2025-06-01");
    const wrapper = mount(NormMetadataFields, {
      props: {
        validTo: inputDate,
      },
    });

    const validToField = wrapper.findComponent(ValidToField);
    expect(validToField.props().value).toStrictEqual(inputDate);
  });

  it("hides valid from and to fields on prototype", () => {
    const mockedIsPrototypeProfile = vi.spyOn(Config, "isPrototypeProfile");
    mockedIsPrototypeProfile.mockReturnValue(true);

    const wrapper = mount(NormMetadataFields, {});

    expect(wrapper.findComponent(ValidFromField).exists()).toBeFalsy();
    expect(wrapper.findComponent(ValidToField).exists()).toBeFalsy();
  });
});
