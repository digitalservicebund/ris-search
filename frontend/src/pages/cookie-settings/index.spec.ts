import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import { RouterLinkStub } from "@vue/test-utils";
import type { VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, it, expect, vi } from "vitest";
import CookieSettings from "~/pages/cookie-settings/index.vue";
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

const cookiesMock = vi.hoisted(() => {
  return {
    get: vi.fn(),
    set: vi.fn(),
    remove: vi.fn(),
  };
});

vi.mock("js-cookie", () => ({
  default: cookiesMock,
}));

const mockUserConsent = ref<boolean | undefined>(undefined);
const mockSetTracking = vi.fn((value: boolean) => {
  mockUserConsent.value = value;
});
const mockInitialize = vi.fn();

vi.mock("~/composables/usePostHog", () => ({
  usePostHog: () => ({
    userConsent: mockUserConsent,
    isBannerVisible: computed(() => mockUserConsent.value === undefined),
    setTracking: mockSetTracking,
    initialize: mockInitialize,
  }),
}));

const factory = async (userConsent: boolean | undefined) => {
  mockUserConsent.value = userConsent;
  return await mountSuspended(CookieSettings, {
    global: {
      stubs: {
        RouterLink: RouterLinkStub,
        Breadcrumb: true,
      },
    },
  });
};

describe("CookieSettings Page", () => {
  const acceptButtonSelector = '[data-testid="settings-accept-cookie"]';
  const declineButtonSelector = '[data-testid="settings-decline-cookie"]';

  const findAcceptButton = (wrapper: VueWrapper) =>
    wrapper.find(acceptButtonSelector);
  const findDeclineButton = (wrapper: VueWrapper) =>
    wrapper.find(declineButtonSelector);

  function assertConsentGiven(wrapper: VueWrapper) {
    expect(wrapper.text()).toContain(
      "Ich bin mit der Nutzung von Analyse-Cookies einverstanden.",
    );
    expect(findAcceptButton(wrapper).exists()).toBe(false);
    expect(findDeclineButton(wrapper).exists()).toBe(true);
  }

  function assertConsentRejected(wrapper: VueWrapper) {
    expect(wrapper.text()).toContain(
      "Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.",
    );
    expect(findAcceptButton(wrapper).exists()).toBe(true);
    expect(findDeclineButton(wrapper).exists()).toBe(false);
  }

  beforeEach(() => {
    mockUserConsent.value = undefined;
    vi.clearAllMocks();
  });

  it("shows that tracking not accepted if userConsent is undefined or false", async () => {
    const wrapper = await factory(false);
    await wrapper.vm.$nextTick();
    assertConsentRejected(wrapper);
  });

  it("shows that tracking is accepted if userConsent is true", async () => {
    cookiesMock.get.mockReturnValue("true");
    const wrapper = await factory(true);
    await wrapper.vm.$nextTick();
    assertConsentGiven(wrapper);
  });

  it("clicking the Accept button sets the tracking to active and changes the UI", async () => {
    cookiesMock.get.mockReturnValue("false");
    const wrapper = await factory(false);
    await wrapper.vm.$nextTick();
    const forms = wrapper.findAll("form");
    const acceptForm = forms.find((form) =>
      form.find('[data-testid="settings-accept-cookie"]').exists(),
    );
    await acceptForm?.trigger("submit");
    await wrapper.vm.$nextTick();
    assertConsentGiven(wrapper);
  });

  it("clicking the Decline button sets the tracking to inactive and changes the UI", async () => {
    cookiesMock.get.mockReturnValue("true");
    const wrapper = await factory(true);
    await wrapper.vm.$nextTick();
    const forms = wrapper.findAll("form");
    const declineForm = forms.find((form) =>
      form.find(declineButtonSelector).exists(),
    );
    await declineForm?.trigger("submit");
    await wrapper.vm.$nextTick();
    assertConsentRejected(wrapper);
  });
});
