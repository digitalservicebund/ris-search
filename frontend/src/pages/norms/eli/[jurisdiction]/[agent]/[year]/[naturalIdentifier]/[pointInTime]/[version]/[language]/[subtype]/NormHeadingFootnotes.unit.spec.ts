import { mount } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import NormHeadingFootnotes from "./NormHeadingFootnotes.vue";

describe("NormHeadingFootnotes.vue", () => {
  it("shows nothing if no footnote is passed", () => {
    const wrapper = mount(NormHeadingFootnotes);
    expect(wrapper.html()).toBe("<!--v-if-->");
  });

  it("shows short footnotes in full", () => {
    const wrapper = mount(NormHeadingFootnotes, {
      props: {
        html: "content",
        textLength: 100,
      },
    });
    expect(wrapper.text()).toBe("content");
    expect(wrapper.find("button").exists()).toBe(false);
  });

  it("shows a button to display longer footnotes", async () => {
    const wrapper = mount(NormHeadingFootnotes, {
      props: {
        html: "longer content",
        textLength: 500,
      },
    });
    expect(wrapper.get("button").text()).toBe("Fußnote anzeigen");
    expect(wrapper.get("div.hidden").attributes("data-show")).toBe("false");

    await wrapper.get("button").trigger("click");
    await nextTick();

    expect(wrapper.get("button").text()).toBe("Fußnote ausblenden");
    expect(wrapper.get("div.hidden").attributes("data-show")).toBe("true");

    await wrapper.get("button").trigger("click");
    await nextTick();

    expect(wrapper.get("button").text()).toBe("Fußnote anzeigen");
    expect(wrapper.get("div.hidden").attributes("data-show")).toBe("false");
  });
});
