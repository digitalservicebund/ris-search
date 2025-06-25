import { mount } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import NormMetadataFields from "~/components/Norm/NormMetadataFields.vue";
import MetadataField from "~/components/MetadataField.vue";

describe("NormMetadataFields.vue", () => {
  it("shows abbreviation", () => {
    const expectedAbbreviation = "FooBar";
    const wrapper = mount(NormMetadataFields, {
      props: {
        abbreviation: expectedAbbreviation,
      },
      global: {
        components: {
          MetadataField,
        },
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
    const wrapper = mount(NormMetadataFields, {
      global: {
        components: {
          MetadataField,
        },
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Abkürzung",
    );
    expect(abbreviationFields).toHaveLength(0);
  });

  it("shows status", () => {
    const expectedStatus = "FooStatus";
    const wrapper = mount(NormMetadataFields, {
      props: {
        status: expectedStatus,
      },
      global: {
        components: {
          MetadataField,
        },
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
    const expectedValidFrom = "2025-01-01";
    const wrapper = mount(NormMetadataFields, {
      props: {
        validFrom: expectedValidFrom,
      },
      global: {
        components: {
          MetadataField,
        },
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
    const wrapper = mount(NormMetadataFields, {
      global: {
        components: {
          MetadataField,
        },
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Gültig ab",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe("-");
  });

  it("shows valid to date", () => {
    const expectedValidTo = "2025-12-01";
    const wrapper = mount(NormMetadataFields, {
      props: {
        validTo: expectedValidTo,
      },
      global: {
        components: {
          MetadataField,
        },
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
    const wrapper = mount(NormMetadataFields, {
      global: {
        components: {
          MetadataField,
        },
      },
    });

    const metadataFields = wrapper.findAllComponents(MetadataField);

    const abbreviationFields = metadataFields.filter(
      (fieldWrapper) => fieldWrapper.props().label === "Gültig bis",
    );

    expect(abbreviationFields).toHaveLength(1);
    expect(abbreviationFields[0].props().value).toBe("-");
  });
});
