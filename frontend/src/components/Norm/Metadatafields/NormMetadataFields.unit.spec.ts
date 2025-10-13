import { mount } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";
import NormMetadataFields from "~/components/Norm/Metadatafields/NormMetadataFields.vue";
import ValidityDatesMetadataFields from "~/components/Norm/Metadatafields/ValidityDatesMetadataFields.vue";
import * as Config from "~/utils/config";
import { parseDateGermanLocalTime } from "~/utils/dateFormatting";
import type { ValidityStatus } from "~/utils/normUtils";
import { findMetadataField } from "~/utils/testing/testUtils";

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

    const abbreviationField = findMetadataField(wrapper, "Abkürzung");
    expect(abbreviationField?.props().value).toBe(expectedAbbreviation);
  });

  it("does not show abbreviation if abbreviation is undefined", () => {
    const wrapper = mount(NormMetadataFields);
    expect(wrapper.find("#abbreviation").exists()).toBeFalsy();
  });

  it.each([
    ["InForce", "Aktuell gültig"],
    ["Expired", "Außer Kraft"],
    ["FutureInForce", "Zukünftig in Kraft"],
  ])("status %s is displayed as %s", (status, expected) => {
    const wrapper = mount(NormMetadataFields, {
      props: {
        status: status as ValidityStatus,
      },
    });

    const statusField = findMetadataField(wrapper, "Status");
    expect(statusField?.props().value).toBe(expected);
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
