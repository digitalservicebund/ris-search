import { mount } from "@vue/test-utils";
import MetadataField from "~/components/MetadataField.vue";

describe("MetadataField.vue", () => {
  it("renders correctly with and without class prop", () => {
    const wrapperWithClass = mount(MetadataField, {
      props: {
        id: "test_id",
        label: "Test Label",
        class: "custom-class",
        value: "Test Data",
      },
    });
    expect(wrapperWithClass.classes()).toContain("custom-class");

    const wrapperWithoutClass = mount(MetadataField, {
      props: {
        id: "test_id",
        label: "Test Label",
        value: "Test Data",
      },
    });
    expect(wrapperWithoutClass.classes()).toStrictEqual([
      "flex",
      "flex-col",
      "gap-4",
    ]);
  });

  it('displays "nicht vorhanden" in text-gray-900 color when data prop is not supplied', () => {
    const wrapper = mount(MetadataField, {
      props: {
        id: "test_id",
        label: "Test Label",
      },
    });
    const dataDiv = wrapper.find("#test_id");
    expect(dataDiv.text()).toBe("nicht vorhanden");
    expect(dataDiv.classes()).toContain("text-gray-900");
  });

  it("links the label with aria-labeledBy", () => {
    const wrapper = mount(MetadataField, {
      props: {
        id: "test_id",
        label: "Test Label",
        value: "Test Data",
      },
    });
    const dataDiv = wrapper.find("#test_id");
    expect(dataDiv.text()).toBe("Test Data");
    expect(dataDiv.attributes("aria-labelledby")).toBe("label-test_id");

    const labelDiv = wrapper.find("#label-test_id");
    expect(labelDiv.text()).toBe("Test Label");
  });
});
