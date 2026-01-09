import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { mount, RouterLinkStub } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
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

const mockUserConsent = ref<boolean | undefined>(undefined);
const mockSetTracking = vi.fn();

vi.mock("~/composables/usePostHog", () => ({
  usePostHog: () => ({
    userConsent: mockUserConsent,
    isBannerVisible: computed(() => mockUserConsent.value === undefined),
    setTracking: mockSetTracking,
  }),
}));

const factory = (userConsent: boolean | undefined) => {
  mockUserConsent.value = userConsent;
  return mount(ConsentBanner, {
    global: {
      stubs: {
        NuxtLink: RouterLinkStub,
      },
    },
  });
};

describe("ConsentBanner", () => {
  const cookieBanner = '[data-testid="cookie-banner"]';
  const declineButton = '[data-testid="decline-cookie"]';

  beforeEach(() => {
    mockUserConsent.value = undefined;
    vi.clearAllMocks();
  });

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

    const forms = wrapper.findAll("form");
    const declineForm = forms.find((form) => form.find(declineButton).exists());
    await declineForm?.trigger("submit");

    expect(mockSetTracking).toHaveBeenCalledWith(false);
  });
});
