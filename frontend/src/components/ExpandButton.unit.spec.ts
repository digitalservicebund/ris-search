import { mount } from "@vue/test-utils";
import ExpandButton from "~/components/ExpandButton.vue";
import GravityUiCircleChevronDown from "~icons/gravity-ui/circle-chevron-down";
import GravityUiCircleChevronDownFill from "~icons/gravity-ui/circle-chevron-down-fill";

describe("ExpandButton", () => {
  it("changes icon from normal to filled on hover", async () => {
    const wrapper = mount(ExpandButton, {
      slots: {
        default: '<div class="slot-content">Slot Content</div>',
      },
    });

    const button = wrapper.get("button");
    await button.trigger("mouseover");
    expect(wrapper.findComponent(GravityUiCircleChevronDownFill).exists()).toBe(
      true,
    );
    await button.trigger("mouseleave");
    expect(wrapper.findComponent(GravityUiCircleChevronDown).exists()).toBe(
      true,
    );
    expect(button.text()).toBe("Slot Content");
  });

  it("changes icon and button when expanded", async () => {
    const wrapper = mount(ExpandButton, {
      props: {
        modelValue: true,
      },
      slots: {
        default: '<div class="slot-content">Slot Content</div>',
      },
    });

    const button = wrapper.get("button");
    await button.trigger("mouseover");
    expect(
      wrapper
        .findComponent(GravityUiCircleChevronDownFill)
        .attributes("data-open"),
    ).toBe("open");
    await button.trigger("mouseleave");
    expect(
      wrapper.findComponent(GravityUiCircleChevronDown).attributes("data-open"),
    ).toBe("open");
    expect(button.text()).toBe("Slot Content");
  });

  it("changes v-model", async () => {
    const wrapper = mount(ExpandButton, {
      props: {
        modelValue: false,
        "onUpdate:modelValue": (value) =>
          wrapper.setProps({ modelValue: value }),
      },
      slots: {
        default: '<div class="slot-content">Slot Content</div>',
      },
    });
    const button = wrapper.get("button");
    await button.trigger("click");
    expect(wrapper.props("modelValue")).toBe(true);
    await button.trigger("click");
    expect(wrapper.props("modelValue")).toBe(false);
  });
});
