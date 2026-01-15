import { mount, RouterLinkStub } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ConsentStatus from "./ConsentStatus.global.vue";

const mockUserConsent = ref<boolean | undefined>(undefined);
const mockSetTracking = vi.fn();
const mockInitialize = vi.fn();

vi.mock("~/composables/usePostHog", () => ({
  CONSENT_COOKIE_NAME: "consent_given",
  usePostHog: () => ({
    userConsent: mockUserConsent,
    setTracking: mockSetTracking,
    initialize: mockInitialize,
  }),
}));

const factory = (userConsent: boolean | undefined) => {
  mockUserConsent.value = userConsent;
  return mount(ConsentStatus, {
    global: {
      stubs: {
        NuxtLink: RouterLinkStub,
        ClientOnly: {
          template: "<slot />",
        },
      },
    },
  });
};

describe("ConsentStatus", () => {
  const acceptButton = '[data-testid="settings-accept-cookie"]';
  const declineButton = '[data-testid="settings-decline-cookie"]';

  beforeEach(() => {
    mockUserConsent.value = undefined;
    vi.clearAllMocks();
  });

  it("renders without error", () => {
    const wrapper = factory(undefined);
    expect(
      wrapper.find('[data-testid="consent-status-wrapper"]').exists(),
    ).toBe(true);
  });

  it("shows accept button when consent is not given", () => {
    const wrapper = factory(false);
    expect(wrapper.find(acceptButton).exists()).toBe(true);
    expect(wrapper.find(declineButton).exists()).toBe(false);
  });

  it("shows decline button when consent is given", () => {
    const wrapper = factory(true);
    expect(wrapper.find(declineButton).exists()).toBe(true);
    expect(wrapper.find(acceptButton).exists()).toBe(false);
  });

  it("displays declined message when consent is false", () => {
    const wrapper = factory(false);
    expect(wrapper.text()).toContain("nicht einverstanden");
  });

  it("displays accepted message when consent is true", () => {
    const wrapper = factory(true);
    expect(wrapper.text()).toContain(
      "Ich bin mit der Nutzung von Analyse-Cookies einverstanden",
    );
  });

  it("calls setTracking(true) when clicking accept button", async () => {
    const wrapper = factory(false);

    const form = wrapper.find("form");
    await form.trigger("submit");

    expect(mockSetTracking).toHaveBeenCalledWith(true);
  });

  it("calls setTracking(false) when clicking decline button", async () => {
    const wrapper = factory(true);

    const form = wrapper.find("form");
    await form.trigger("submit");

    expect(mockSetTracking).toHaveBeenCalledWith(false);
  });
});
