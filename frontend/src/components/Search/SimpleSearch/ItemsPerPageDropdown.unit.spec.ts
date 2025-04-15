import type { VueWrapper } from "@vue/test-utils";
import { mount, RouterLinkStub } from "@vue/test-utils";

import { beforeEach, describe, expect, it } from "vitest";
import { createTestingPinia } from "@pinia/testing";
import { useSimpleSearchParamsStore } from "@/stores/searchParams";
import ItemsPerPageDropdown from "./ItemsPerPageDropdown.vue";
import PrimeVue from "primevue/config";

describe("items per page dropdown", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(ItemsPerPageDropdown, {
      stubs: {
        RouterLink: RouterLinkStub,
      },
      global: {
        plugins: [createTestingPinia({ stubActions: false }), PrimeVue],
      },
    });
  });

  it("updates the items per page", async () => {
    const store = useSimpleSearchParamsStore(); // uses testing pinia
    expect(store.itemsPerPage).toBe(10);
    wrapper.findComponent({ name: "Select" }).setValue("50");
    await nextTick();
    expect(store.itemsPerPage).toBe(50);
  });

  it("displays the store value", async () => {
    const store = useSimpleSearchParamsStore();
    store.itemsPerPage = 50;
    await nextTick();
    expect(wrapper.find('span[role="combobox"]').element.innerHTML).toBe("50");
    expect(
      wrapper.findComponent({ name: "Select" }).props("options"),
    ).toHaveLength(3);
  });

  it("displays custom values", async () => {
    const store = useSimpleSearchParamsStore();
    store.itemsPerPage = 42;
    await nextTick();
    expect(wrapper.find('span[role="combobox"]').element.innerHTML).toBe("42");
    expect(
      wrapper.findComponent({ name: "Select" }).props("options"),
    ).toHaveLength(4);
  });
});
