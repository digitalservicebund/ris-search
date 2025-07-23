import { mount } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import ValidFromField from "~/components/Norm/Metadatafields/ValidFromField.vue";

describe("ValidFromField.vue", () => {
  it("displays formatted date if present", () => {
    const wrapper = mount(ValidFromField, {
      props: {
        value: parseDateGermanLocalTime("2025-01-01"),
      },
    });

    expect(wrapper.html()).toContain("Gültig ab");
    expect(wrapper.html()).toContain("01.01.2025");
  });

  it("shows placeholder if date is undefined", () => {
    const wrapper = mount(ValidFromField);

    expect(wrapper.html()).toContain("Gültig ab");
    expect(wrapper.html()).toContain("-");
  });
});
