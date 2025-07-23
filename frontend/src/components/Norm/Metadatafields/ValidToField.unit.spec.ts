import { mount } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import ValidToField from "~/components/Norm/Metadatafields/ValidToField.vue";

describe("ValidToField.vue", () => {
  it("displays formatted date if present", () => {
    const wrapper = mount(ValidToField, {
      props: {
        value: parseDateGermanLocalTime("2025-06-01"),
      },
    });

    expect(wrapper.html()).toContain("Gültig bis");
    expect(wrapper.html()).toContain("01.06.2025");
  });

  it("shows placeholder if date is undefined", () => {
    const wrapper = mount(ValidToField);

    expect(wrapper.html()).toContain("Gültig bis");
    expect(wrapper.html()).toContain("-");
  });
});
