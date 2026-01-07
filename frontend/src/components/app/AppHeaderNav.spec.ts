import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppHeaderNav from "./AppHeaderNav.vue";

const NuxtLinkStub = {
  name: "NuxtLink",
  props: ["to"],
  template: `<a :href="typeof to === 'string' ? to : '/'" v-bind="$attrs"><slot /></a>`,
};

describe("AppHeaderNav", () => {
  it('emits "selectItem" for each NuxtLink that has been clicked', async () => {
    const user = userEvent.setup();
    const { emitted } = await renderSuspended(AppHeaderNav, {
      props: {
        listClass: "test-class",
      },
      global: {
        stubs: {
          NuxtLink: NuxtLinkStub,
        },
      },
    });

    const searchLinks = screen.getAllByRole("link");
    for (const link of searchLinks) {
      await user.click(link);
    }
    expect(emitted("selectItem")).toBeTruthy();
    expect(emitted("selectItem")?.length).toBe(searchLinks.length);
  });
});
