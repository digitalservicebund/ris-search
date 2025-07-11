import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { createTestingPinia } from "@pinia/testing";
import { mount, RouterLinkStub } from "@vue/test-utils";
import { vi } from "vitest";
import ConsentBanner from "./ConsentBanner.vue";
import { getPostHogConfig } from "~/utils/testing/postHogUtils";

const { useRuntimeConfigMock } = vi.hoisted(() => {
  return {
    useRuntimeConfigMock: vi.fn(() => {
      return getPostHogConfig("key", "host", "123");
    }),
  };
});

mockNuxtImport("useRuntimeConfig", () => {
  return useRuntimeConfigMock;
});

const factory = (userConsent: boolean | undefined) =>
  mount(ConsentBanner, {
    global: {
      plugins: [
        createTestingPinia({
          stubActions: false,
          initialState: {
            postHog: {
              userConsent: userConsent,
            },
          },
        }),
      ],
      stubs: {
        NuxtLink: RouterLinkStub,
      },
    },
  });

describe("ConsentBanner.vue", () => {
  const cookieBanner = '[data-testid="cookie-banner"]';
  const declineButton = '[data-testid="decline-cookie"]';

  it("shows the banner when user has not given consent yet", async () => {
    const wrapper = factory(undefined);
    expect(wrapper.find(cookieBanner).exists()).toBe(true);
  });

  it("hides the banner when user has already given consent", async () => {
    const wrapper = factory(true);
    expect(wrapper.find(cookieBanner).exists()).toBe(false);
  });

  it("calls handleSetTracking when clicking the Decline button", async () => {
    const wrapper = factory(undefined);
    const setTrackingSpy = vi.spyOn(wrapper.vm, "handleSetTracking");
    await wrapper.find(declineButton).trigger("click");
    expect(setTrackingSpy).toHaveBeenCalledWith(false);
  });
});
