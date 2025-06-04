import { mount } from "@vue/test-utils";
import NormVersionListRow from "./NormVersionListRow.vue";
import { data } from "./NormVersions.spec.data";

function getWrapper() {
  return mount(NormVersionListRow, {
    props: {
      item: data.member[0].item,
    },
    global: {
      stubs: {
        RouterLink: {
          template: `<a :href="$attrs.to"><slot /></a>`,
        },
      },
    },
  });
}

describe("NormVersionListRow", () => {
  it("shows the dates and status", () => {
    const wrapper = getWrapper();
    expect(wrapper.get("#valid-from").text()).toEqual("01.01.2020");
    expect(wrapper.get("#valid-to").text()).toEqual("nicht vorhanden");
    expect(wrapper.get("#status").text()).toEqual("In Kraft");
  });

  it("links to the respective expression ELI", async () => {
    const wrapper = getWrapper();
    const link = wrapper.get("a");
    expect(link.text()).toBe("01.01.2020");
    expect(link.attributes("href")).toBe(
      "/norms/" + data.member[0].item.workExample.legislationIdentifier,
    );
  });
});
