import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { mount, RouterLinkStub } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import AppHeaderNav from "./AppHeaderNav.vue";

describe("AppHeaderNav", () => {
  it('emits "selectItem" for each NuxtLink that has been clicked', async () => {
    const wrapper = mount(AppHeaderNav, {
      props: {
        listClass: "test-class",
      },
      global: {
        stubs: {
          NuxtLink: RouterLinkStub,
        },
      },
    });

    const searchLinks = wrapper.findAll("a");
    for (const link of searchLinks) {
      await link.trigger("click");
    }
    expect(wrapper.emitted("selectItem")).toBeTruthy();
    expect(wrapper.emitted("selectItem")?.length).toBe(searchLinks.length);
  });
});
