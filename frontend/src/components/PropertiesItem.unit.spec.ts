import { mount } from "@vue/test-utils";
import PropertiesItem from "~/components/PropertiesItem.vue";

describe("PropertiesItem.vue", () => {
  it('displays "nicht vorhanden" in text-gray-900 color when data prop is not supplied', () => {
    const wrapper = mount(PropertiesItem, {
      props: {
        label: "Test Label",
      },
    });
    const definitionDiv = wrapper.find("dd");
    expect(definitionDiv.text()).toBe("nicht vorhanden");
    expect(definitionDiv.attributes("data-empty")).toBe("empty");
  });

  it("renders the slot if provided", () => {
    const wrapper = mount(PropertiesItem, {
      slots: {
        default: "Default Slot",
      },
      props: {
        label: "Test Label",
      },
    });

    const definitionDiv = wrapper.find("dd");
    expect(definitionDiv.text()).toBe("Default Slot");
    expect(definitionDiv.classes()).toContain("ris-label1-regular");
  });
});
