import { mount } from "@vue/test-utils";
import { describe, it, expect, vi, afterEach } from "vitest";
import ArticleMetadataFields from "~/components/Norm/Metadatafields/ArticleMetadataFields.vue";
import ValidFromField from "~/components/Norm/Metadatafields/ValidFromField.vue";
import ValidToField from "~/components/Norm/Metadatafields/ValidToField.vue";
import * as Config from "~/utils/config";

describe("ArticleMetadataFields.vue", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("hides valid from and to fields on prototype", () => {
    const mockedIsPrototypeProfile = vi.spyOn(Config, "isPrototypeProfile");
    mockedIsPrototypeProfile.mockReturnValue(true);

    const wrapper = mount(ArticleMetadataFields, {});

    expect(wrapper.findComponent(ValidFromField).exists()).toBeFalsy();
    expect(wrapper.findComponent(ValidToField).exists()).toBeFalsy();
  });

  it("shows valid-from metadata field", () => {
    const inputDate = parseDateGermanLocalTime("2025-01-01");
    const wrapper = mount(ArticleMetadataFields, {
      props: {
        validFrom: inputDate,
      },
    });

    const validFromField = wrapper.findComponent(ValidFromField);
    expect(validFromField.props().value).toStrictEqual(inputDate);
  });

  it("shows valid-to metadata field", () => {
    const inputDate = parseDateGermanLocalTime("2025-06-01");
    const wrapper = mount(ArticleMetadataFields, {
      props: {
        validTo: inputDate,
      },
    });

    const validToField = wrapper.findComponent(ValidToField);
    expect(validToField.props().value).toStrictEqual(inputDate);
  });
});
