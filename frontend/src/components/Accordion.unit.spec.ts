import { mount } from "@vue/test-utils";
import Accordion from "~/components/Accordion.vue";
import GravityUiCircleChevronDown from "~icons/gravity-ui/circle-chevron-down";
import GravityUiCircleChevronDownFill from "~icons/gravity-ui/circle-chevron-down-fill";
import GravityUiCircleChevronUp from "~icons/gravity-ui/circle-chevron-up";
import GravityUiCircleChevronUpFill from "~icons/gravity-ui/circle-chevron-up-fill";

describe("Accordion.vue", () => {
  it("changes icon from normal to filled on hover", async () => {
    const wrapper = mount(Accordion, {
      props: {
        headerCollapsed: "Show More",
        headerExpanded: "Show Less",
      },
      slots: {
        default: '<div class="slot-content">Slot Content</div>',
      },
    });

    const header = wrapper.find(".flex.flex-row.space-x-8.py-24");
    await header.trigger("mouseover");
    expect(wrapper.findComponent(GravityUiCircleChevronDownFill).exists()).toBe(
      true,
    );
    await header.trigger("mouseleave");
    expect(wrapper.findComponent(GravityUiCircleChevronDown).exists()).toBe(
      true,
    );
    expect(header.text()).toContain("Show More");
    expect(wrapper.find(".slot-content").text()).toContain("Slot Content");
  });

  it("changes icon and header when expanded", async () => {
    const wrapper = mount(Accordion, {
      props: {
        headerCollapsed: "Show More",
        headerExpanded: "Show Less",
        value: "0",
      },
      slots: {
        default: '<div class="slot-content">Slot Content</div>',
      },
    });

    const header = wrapper.find(".flex.flex-row.space-x-8.py-24");
    await header.trigger("mouseover");
    expect(wrapper.findComponent(GravityUiCircleChevronUpFill).exists()).toBe(
      true,
    );
    await header.trigger("mouseleave");
    expect(wrapper.findComponent(GravityUiCircleChevronUp).exists()).toBe(true);
    expect(header.text()).toContain("Show Less");
  });
});
