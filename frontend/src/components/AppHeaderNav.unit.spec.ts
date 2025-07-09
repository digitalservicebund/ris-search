import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { mount, RouterLinkStub } from "@vue/test-utils";
import { describe, it, expect, vi } from "vitest";
import AppHeaderNav from "./AppHeaderNav.vue";

const { mockRuntimeConfig } = vi.hoisted(() => ({
  mockRuntimeConfig: vi.fn().mockReturnValue({
    public: {
      authEnabled: true,
    },
  }),
}));

mockNuxtImport("useRuntimeConfig", () => {
  return mockRuntimeConfig;
});

vi.mock("~/components/LoginActions.vue", () => ({
  default: {
    name: "LoginActions",
    template: '<div data-testid="login-actions">Login Actions Component</div>',
  },
}));

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

  it("displays LoginActions when config.public.authEnabled is true", () => {
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

    // Check if the mocked LoginActions component has been rendered
    expect(wrapper.find('[data-testid="login-actions"]').exists()).toBe(true);
  });

  it("does not display LoginActions when config.public.authEnabled is false", () => {
    mockRuntimeConfig.mockReturnValueOnce({
      public: {
        authEnabled: false,
      },
    });

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

    expect(wrapper.find('[data-testid="login-actions"]').exists()).toBe(false);

    vi.restoreAllMocks();
  });
});
