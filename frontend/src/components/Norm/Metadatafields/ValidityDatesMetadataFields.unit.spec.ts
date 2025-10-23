import { mount } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import ValidityDatesMetadataFields from "~/components/Norm/Metadatafields/ValidityDatesMetadataFields.vue";
import { findMetadataField } from "~/tests/testUtils";
import { parseDateGermanLocalTime } from "~/utils/dateFormatting";

describe("ValidityDatesMetadataFields.vue", () => {
  it("displays formatted valid from date if present", () => {
    const wrapper = mount(ValidityDatesMetadataFields, {
      props: {
        validFrom: parseDateGermanLocalTime("2025-01-01"),
      },
    });

    const validFromField = findMetadataField(wrapper, "Gültig ab");
    expect(validFromField?.props().value).toBe("01.01.2025");
  });

  it("shows placeholder if valid from date is undefined", () => {
    const wrapper = mount(ValidityDatesMetadataFields);

    const validFromField = findMetadataField(wrapper, "Gültig ab");
    expect(validFromField?.props().value).toBe("-");
  });

  it("displays formatted valid to date if present", () => {
    const wrapper = mount(ValidityDatesMetadataFields, {
      props: {
        validTo: parseDateGermanLocalTime("2025-06-01"),
      },
    });

    const validFromField = findMetadataField(wrapper, "Gültig bis");
    expect(validFromField?.props().value).toBe("01.06.2025");
  });

  it("shows placeholder if valid to date is undefined", () => {
    const wrapper = mount(ValidityDatesMetadataFields);

    const validFromField = findMetadataField(wrapper, "Gültig bis");
    expect(validFromField?.props().value).toBe("-");
  });
});
