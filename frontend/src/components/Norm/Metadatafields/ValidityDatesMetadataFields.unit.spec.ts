import { mount, type VueWrapper } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import MetadataField from "~/components/MetadataField.vue";
import ValidityDatesMetadataFields from "~/components/Norm/Metadatafields/ValidityDatesMetadataFields.vue";

function findMetadataField(wrapper: VueWrapper, label: string) {
  const metadataFields = wrapper.findAllComponents(MetadataField);

  const result = metadataFields.filter(
    (fieldWrapper) => fieldWrapper.props().label === label,
  );

  expect(result).toHaveLength(1);
  return result[0];
}

describe("ValidityDatesMetadataFields.vue", () => {
  it("displays formatted valid from date if present", () => {
    const wrapper = mount(ValidityDatesMetadataFields, {
      props: {
        validFrom: parseDateGermanLocalTime("2025-01-01"),
      },
    });

    const validFromField = findMetadataField(wrapper, "Gültig ab");
    expect(validFromField.props().value).toBe("01.01.2025");
  });

  it("shows placeholder if valid from date is undefined", () => {
    const wrapper = mount(ValidityDatesMetadataFields);

    const validFromField = findMetadataField(wrapper, "Gültig ab");
    expect(validFromField.props().value).toBe("-");
  });

  it("displays formatted valid to date if present", () => {
    const wrapper = mount(ValidityDatesMetadataFields, {
      props: {
        validTo: parseDateGermanLocalTime("2025-06-01"),
      },
    });

    const validFromField = findMetadataField(wrapper, "Gültig bis");
    expect(validFromField.props().value).toBe("01.06.2025");
  });

  it("shows placeholder if valid to date is undefined", () => {
    const wrapper = mount(ValidityDatesMetadataFields);

    const validFromField = findMetadataField(wrapper, "Gültig bis");
    expect(validFromField.props().value).toBe("-");
  });
});
