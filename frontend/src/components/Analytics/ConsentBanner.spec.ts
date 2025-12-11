import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { createTestingPinia } from "@pinia/testing";
import { mount, RouterLinkStub } from "@vue/test-utils";
import { vi } from "vitest";
import ConsentBanner from "./ConsentBanner.vue";
import { getPostHogConfig } from "~/tests/postHogUtils";

const { useRuntimeConfigMock } = vi.hoisted(() => {
  return {
    useRuntimeConfigMock: vi.fn(() => {
      return getPostHogConfig("key", "host");
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
          stubActions: true,
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

describe("ConsentBanner", () => {
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

  it("sets tracking when clicking the Decline button", async () => {
    const wrapper = factory(undefined);
    const store = usePostHogStore();

    const forms = wrapper.findAll("form");
    const declineForm = forms.find((form) => form.find(declineButton).exists());
    await declineForm?.trigger("submit");

    expect(store.setTracking).toHaveBeenCalledWith(false);
  });
});
