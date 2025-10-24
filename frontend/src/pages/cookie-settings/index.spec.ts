import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { createTestingPinia } from "@pinia/testing";
import { mount, RouterLinkStub } from "@vue/test-utils";
import type { VueWrapper } from "@vue/test-utils";
import { describe, it, expect, vi } from "vitest";
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

const factory = (userConsent: boolean | undefined) =>
  mount(CookieSettings, {
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
        RouterLink: RouterLinkStub,
        Breadcrumb: true,
      },
    },
  });

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
    vi.clearAllMocks();
  });

  it("renders the noscript content", () => {
    const wrapper = factory(false);
    expect(wrapper.html()).toContain("</noscript>");
  });

  it("shows that tracking not accepted if userConsent is undefined or false", async () => {
    const wrapper = factory(false);
    await wrapper.vm.$nextTick();
    assertConsentRejected(wrapper);
  });

  it("shows that tracking is accepted if userConsent is true", async () => {
    const wrapper = factory(true);
    await wrapper.vm.$nextTick();
    assertConsentGiven(wrapper);
  });

  it("clicking the Accept button sets the tracking to active and changes the UI", async () => {
    const wrapper = factory(false);
    await wrapper.vm.$nextTick();
    const setTrackingSpy = vi.spyOn(wrapper.vm, "handleSetTracking");
    await findAcceptButton(wrapper).trigger("click");
    await wrapper.vm.$nextTick();
    expect(setTrackingSpy).toHaveBeenCalledWith(true);
    assertConsentGiven(wrapper);
  });

  it("clicking the Decline button sets the tracking to inactive and changes the UI", async () => {
    const wrapper = factory(true);
    await wrapper.vm.$nextTick();
    const setTrackingSpy = vi.spyOn(wrapper.vm, "handleSetTracking");
    await findDeclineButton(wrapper).trigger("click");
    await wrapper.vm.$nextTick();
    expect(setTrackingSpy).toHaveBeenCalledWith(false);
    assertConsentRejected(wrapper);
  });
});
